package com.example.orderfood.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.orderfood.data.model.Food
import com.example.orderfood.data.repository.ApiResult
import com.example.orderfood.data.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FoodUiState {
    object Loading : FoodUiState()
    data class Success(val foods: List<Food>) : FoodUiState()
    data class Error(val message: String) : FoodUiState()
    object Empty : FoodUiState()
}

@HiltViewModel
class FoodViewModel @Inject constructor(private val repository: FoodRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<FoodUiState>(FoodUiState.Loading)
    val uiState: StateFlow<FoodUiState> = _uiState

    init { loadFoods() }

    fun loadFoods() {
        viewModelScope.launch {
            _uiState.value = FoodUiState.Loading
            when (val result = repository.getFoods()) {
                is ApiResult.Success -> {
                    _uiState.value = if (result.data.isEmpty())
                        FoodUiState.Empty else FoodUiState.Success(result.data)
                }
                is ApiResult.Error -> _uiState.value = FoodUiState.Error(result.message)
            }
        }
    }
}