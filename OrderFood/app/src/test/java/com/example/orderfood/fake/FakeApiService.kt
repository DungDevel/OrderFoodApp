package com.example.orderfood.fake

import com.example.orderfood.data.model.Food
import com.example.orderfood.data.model.OrderRequest
import com.example.orderfood.data.model.OrderResponse
import com.example.orderfood.data.network.ApiService

class FakeApiService(
    private var foods: List<Food> = emptyList(),
    private var orders: List<OrderResponse> = emptyList(),
    private var shouldThrowOnGetFoods: Boolean = false,
    private var shouldThrowOnPlaceOrder: Boolean = false
) : ApiService {

    override suspend fun getFoods(): List<Food> {
        if (shouldThrowOnGetFoods) throw java.io.IOException("Mất kết nối")
        return foods
    }

    override suspend fun placeOrder(order: OrderRequest): OrderResponse {
        if (shouldThrowOnPlaceOrder) throw java.io.IOException("Mất kết nối")
        return OrderResponse(
            id = "fake-id-${orders.size + 1}",
            items = order.items,
            totalPrice = order.totalPrice,
            createdAt = order.createdAt
        )
    }

    override suspend fun getOrders(): List<OrderResponse> = orders
}