package com.example.orderfood.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.orderfood.data.model.CartItem
import com.example.orderfood.data.model.Food
import com.example.orderfood.data.model.OrderItemDto
import com.example.orderfood.data.model.OrderRequest
import com.example.orderfood.data.repository.ApiResult
import com.example.orderfood.data.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CheckoutState {
    object Idle : CheckoutState()
    object Loading : CheckoutState()
    object Success : CheckoutState()
    data class Error(val message: String) : CheckoutState()
}

@HiltViewModel
class CartViewModel @Inject constructor(private val repository: FoodRepository) : ViewModel() {

    private val cartMap = mutableStateMapOf<Int, CartItem>()
    val items: List<CartItem> get() = cartMap.values.toList()

    val totalPrice: Long get() = cartMap.values.sumOf { it.totalPrice }
    val totalQuantity: Int get() = cartMap.values.sumOf { it.quantity }

    var checkoutState by mutableStateOf<CheckoutState>(CheckoutState.Idle)
        private set

    fun addToCart(food: Food) {
        if (!food.available) return
        val current = cartMap[food.id]
        cartMap[food.id] = current?.copy(quantity = current.quantity + 1)
            ?: CartItem(food, 1)
    }

    fun increase(foodId: Int) {
        val current = cartMap[foodId] ?: return
        cartMap[foodId] = current.copy(quantity = current.quantity + 1)
    }

    fun decrease(foodId: Int) {
        val current = cartMap[foodId] ?: return
        if (current.quantity <= 1) cartMap.remove(foodId)
        else cartMap[foodId] = current.copy(quantity = current.quantity - 1)
    }

    fun removeItem(foodId: Int) = cartMap.remove(foodId)

    fun clearCart() {
        cartMap.clear()
        checkoutState = CheckoutState.Idle
    }

    fun checkout() {
        if (cartMap.isEmpty()) return
        if (checkoutState is CheckoutState.Loading) return

        viewModelScope.launch {
            checkoutState = CheckoutState.Loading
            val order = OrderRequest(
                items = cartMap.values.map {
                    OrderItemDto(it.food.id, it.food.name, it.quantity, it.food.price)
                },
                totalPrice = totalPrice,
                createdAt = System.currentTimeMillis()

            )
            when (val result = repository.placeOrder(order)) {
                is ApiResult.Success -> {
                    checkoutState = CheckoutState.Success
                    cartMap.clear()
                }
                is ApiResult.Error -> checkoutState = CheckoutState.Error(result.message)
            }
        }
    }

    fun resetCheckoutState() { checkoutState = CheckoutState.Idle }
}