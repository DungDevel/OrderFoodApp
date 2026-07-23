package com.example.orderfood.data.model

data class CartItem(
    val food: Food,
    val quantity: Int
) {
    val totalPrice: Long get() = food.price * quantity
}