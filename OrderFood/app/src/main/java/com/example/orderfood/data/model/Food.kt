package com.example.orderfood.data.model

data class Food(
    val id: Int,
    val name: String,
    val price: Long,
    val image: String,
    val category: String = "",
    val available: Boolean = true
)