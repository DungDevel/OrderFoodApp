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

class FoodViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `goi api dsach mon an thanh cong tra ve Success`() = runTest {
        val fakeApi = FakeApiService(foods = listOf(TestData.phoBo, TestData.comTam))
        val viewModel = FoodViewModel(FoodRepository(fakeApi))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is FoodUiState.Success)
        assertEquals(2, (state as FoodUiState.Success).foods.size)
    }

    @Test
    fun `danh sach rong tra ve trang thai Empty`() = runTest {
        val fakeApi = FakeApiService(foods = emptyList())
        val viewModel = FoodViewModel(FoodRepository(fakeApi))
        advanceUntilIdle()

        assertEquals(FoodUiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun `loi mang tra ve trang thai Error`() = runTest {
        val fakeApi = FakeApiService(shouldThrowOnGetFoods = true)
        val viewModel = FoodViewModel(FoodRepository(fakeApi))
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is FoodUiState.Error)
    }}