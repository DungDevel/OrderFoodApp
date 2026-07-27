package com.example.orderfood.data.repository

import com.example.orderfood.data.model.Food
import com.example.orderfood.data.model.OrderRequest
import com.example.orderfood.data.model.OrderResponse
import com.example.orderfood.data.network.ApiService
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String) : ApiResult<Nothing>()
}

@Singleton
class FoodRepository @Inject constructor(
    private val api: ApiService
) {

    suspend fun getOrders(): ApiResult<List<OrderResponse>> = try {
        ApiResult.Success(api.getOrders()
            .sortedByDescending { it.createdAt }
            .take(15)
        )
    } catch (e: IOException) {
        ApiResult.Error("Không kết nối được máy chủ. Kiểm tra mạng và thử lại.")
    } catch (e: Exception) {
        ApiResult.Error("Không tải được lịch sử đơn hàng: ${e.message}")
    }
    suspend fun getFoods(): ApiResult<List<Food>> = try {
        ApiResult.Success(api.getFoods())
    } catch (e: IOException) {
        ApiResult.Error("Không kết nối được máy chủ. Kiểm tra mạng và thử lại.")
    } catch (e: Exception) {
        ApiResult.Error("Đã có lỗi xảy ra: ${e.message}")
    }

    suspend fun placeOrder(order: OrderRequest): ApiResult<OrderResponse> = try {
        ApiResult.Success(api.placeOrder(order))
    } catch (e: IOException) {
        ApiResult.Error("Không kết nối được máy chủ khi đặt hàng.")
    } catch (e: Exception) {
        ApiResult.Error("Đặt hàng thất bại: ${e.message}")
    }
}