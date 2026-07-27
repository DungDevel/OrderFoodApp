package com.example.orderfood.data.model

data class OrderRequest(
    val items: List<OrderItemDto>,
    val totalPrice: Long,
    val createdAt: Long
)

data class OrderItemDto(
    val foodId: Int,
    val name: String,
    val quantity: Int,
    val price: Long
)

data class OrderResponse(
    val id: String,
    val items: List<OrderItemDto>,
    val totalPrice: Long,
    val createdAt: Long
)