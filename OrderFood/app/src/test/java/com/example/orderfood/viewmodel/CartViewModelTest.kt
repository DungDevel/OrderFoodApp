package com.example.orderfood.viewmodel

import com.example.orderfood.MainDispatcherRule
import com.example.orderfood.data.repository.FoodRepository
import com.example.orderfood.fake.FakeApiService
import com.example.orderfood.fake.TestData
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class CartViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun createViewModel(fakeApi: FakeApiService = FakeApiService()): CartViewModel {
        val repository = FoodRepository(fakeApi)
        return CartViewModel(repository)
    }

    @Test
    fun `them mon thanh cong`() {
        val viewModel = createViewModel()
        viewModel.addToCart(TestData.phoBo)

        assertEquals(1, viewModel.items.size)
        assertEquals(1, viewModel.totalQuantity)
        assertEquals(45000L, viewModel.totalPrice)
    }

    @Test
    fun `them 1 mon nhieu lan khong tao dong moi`() {
        val viewModel = createViewModel()
        viewModel.addToCart(TestData.phoBo)
        viewModel.addToCart(TestData.phoBo)
        viewModel.addToCart(TestData.phoBo)

        assertEquals(1, viewModel.items.size)          // vẫn chỉ 1 dòng
        assertEquals(3, viewModel.items.first().quantity)
        assertEquals(135000L, viewModel.totalPrice)
    }

    @Test
    fun `khong the them mon da het hang`() {
        val viewModel = createViewModel()
        viewModel.addToCart(TestData.traDaHetHang)

        assertTrue(viewModel.items.isEmpty())
    }

    @Test
    fun `giam so luong ve 0 se tu dong xoa mon khoi gio`() {
        val viewModel = createViewModel()
        viewModel.addToCart(TestData.phoBo)   // quantity = 1
        viewModel.decrease(TestData.phoBo.id) // 1 -> xoá

        assertTrue(viewModel.items.isEmpty())
        assertEquals(0L, viewModel.totalPrice)
    }

    @Test
    fun `tinh tong tien mon an`() {
        val viewModel = createViewModel()
        viewModel.addToCart(TestData.phoBo)   // 45000
        viewModel.addToCart(TestData.comTam)  // 40000
        viewModel.addToCart(TestData.comTam)  // +40000

        assertEquals(3, viewModel.totalQuantity)
        assertEquals(125000L, viewModel.totalPrice)
    }

    @Test
    fun `dat hang khi gio hang rong khong lam gi ca`() = runTest {
        val viewModel = createViewModel()
        viewModel.checkout()
        advanceUntilIdle()

        assertEquals(CheckoutState.Idle, viewModel.checkoutState)
    }

    @Test
    fun `goi mon duoc se xoa gio hang va chuyen trang thai Success`() = runTest {
        val viewModel = createViewModel()
        viewModel.addToCart(TestData.phoBo)

        viewModel.checkout()
        advanceUntilIdle()

        assertEquals(CheckoutState.Success, viewModel.checkoutState)
        assertTrue(viewModel.items.isEmpty())   // giỏ hàng rỗng sau khi thành công
    }

    @Test
    fun `goi mon k dc se giu nguyen gio hang khong bi mat`() = runTest {
        val fakeApi = FakeApiService(shouldThrowOnPlaceOrder = true)
        val viewModel = createViewModel(fakeApi)
        viewModel.addToCart(TestData.phoBo)

        viewModel.checkout()
        advanceUntilIdle()

        assertTrue(viewModel.checkoutState is CheckoutState.Error)
        assertEquals(1, viewModel.items.size)   // K bị mất giỏ hàng khi lỗi
    }

    @Test
    fun `bam dat hang 2 lan lien tiep khi dang loading khong tao 2 don`() = runTest {
        val viewModel = createViewModel()
        viewModel.addToCart(TestData.phoBo)

        viewModel.checkout()          // 1: Loading
        viewModel.checkout()          // 2: k nhấn được
        advanceUntilIdle()

        assertEquals(CheckoutState.Success, viewModel.checkoutState)
    }
}