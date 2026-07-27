package com.example.orderfood.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.orderfood.data.model.OrderResponse
import com.example.orderfood.data.repository.ApiResult
import com.example.orderfood.data.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class OrderHistoryUiState {
    object Loading : OrderHistoryUiState()
    data class Success(val orders: List<OrderResponse>) : OrderHistoryUiState()
    object Empty : OrderHistoryUiState()
    data class Error(val message: String) : OrderHistoryUiState()
}

@HiltViewModel
class OrderHistoryViewModel @Inject constructor(
    private val repository: FoodRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<OrderHistoryUiState>(OrderHistoryUiState.Loading)
    val uiState: StateFlow<OrderHistoryUiState> = _uiState

    init { loadOrders() }

    fun loadOrders() {
        viewModelScope.launch {
            _uiState.value = OrderHistoryUiState.Loading
            when (val result = repository.getOrders()) {
                is ApiResult.Success -> {
                    _uiState.value = if (result.data.isEmpty())
                        OrderHistoryUiState.Empty else OrderHistoryUiState.Success(result.data)
                }
                is ApiResult.Error -> _uiState.value = OrderHistoryUiState.Error(result.message)
            }
        }
    }
}