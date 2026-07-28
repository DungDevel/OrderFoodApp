package com.example.orderfood.data.repository

import com.example.orderfood.data.model.OrderResponse
import com.example.orderfood.fake.FakeApiService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class FoodRepositoryTest {

    private fun order(id: String, createdAt: Long) =
        OrderResponse(id = id, items = emptyList(), totalPrice = 0, createdAt = createdAt)

    @Test
    fun `lay 15 don hang trong server co nhieu hon`() = runTest {
        val manyOrders = (1..30).map { order("order$it", createdAt = it.toLong()) }
        val fakeApi = FakeApiService(orders = manyOrders)
        val repository = FoodRepository(fakeApi)

        val result = repository.getOrders()

        assertTrue(result is ApiResult.Success)
        assertEquals(15, (result as ApiResult.Success).data.size)
    }

    @Test
    fun `don hang moi nhat o dau tien`() = runTest {
        val orders = listOf(
            order("cu", createdAt = 1000L),
            order("moi_nhat", createdAt = 9000L),
            order("giua", createdAt = 5000L)
        )
        val fakeApi = FakeApiService(orders = orders)
        val repository = FoodRepository(fakeApi)

        val result = repository.getOrders() as ApiResult.Success
        assertEquals("moi_nhat", result.data.first().id)
    }

    @Test
    fun `it hon 15 don thi tra ve dung so luong don hang`() = runTest {
        val orders = (1..5).map { order("order$it", createdAt = it.toLong()) }
        val fakeApi = FakeApiService(orders = orders)
        val repository = FoodRepository(fakeApi)

        val result = repository.getOrders() as ApiResult.Success
        assertEquals(5, result.data.size)
    }
}