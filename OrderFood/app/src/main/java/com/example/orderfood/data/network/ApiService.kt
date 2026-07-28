package com.example.orderfood.data.network

import com.example.orderfood.data.model.Food
import com.example.orderfood.data.model.OrderRequest
import com.example.orderfood.data.model.OrderResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("foods")
    suspend fun getFoods(): List<Food>

    @POST("orders")
    suspend fun placeOrder(@Body order: OrderRequest): OrderResponse

    @GET("orders")
    suspend fun getOrders(): List<OrderResponse>
}