package com.example.orderfood.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.orderfood.data.model.OrderResponse
import com.example.orderfood.utils.toVnd
import com.example.orderfood.viewmodel.OrderHistoryUiState
import com.example.orderfood.viewmodel.OrderHistoryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OrderHistoryScreen(
    viewModel: OrderHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is OrderHistoryUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))

            is OrderHistoryUiState.Error -> Column(
                Modifier.align(Alignment.Center).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(state.message, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                Spacer(Modifier.height(12.dp))
                Button(onClick = { viewModel.loadOrders() }) { Text("Thử lại") }
            }

            is OrderHistoryUiState.Empty -> Text(
                "Bạn chưa có đơn hàng nào",
                Modifier.align(Alignment.Center)
            )

            is OrderHistoryUiState.Success -> LazyColumn(
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.orders, key = { it.id }) { order ->
                    OrderCard(order)
                }
            }
        }
    }
}

@Composable
private fun OrderCard(order: OrderResponse) {
    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Đơn #${order.id.take(6)}", fontWeight = FontWeight.Bold)
                Text(formatOrderDate(order.createdAt), style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(8.dp))
            order.items.forEach { item ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${item.name} x${item.quantity}", style = MaterialTheme.typography.bodyMedium)
                    Text((item.price * item.quantity).toVnd(), style = MaterialTheme.typography.bodyMedium)
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Tổng cộng", fontWeight = FontWeight.Bold)
                Text(order.totalPrice.toVnd(), fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun formatOrderDate(millis: Long): String {
    if (millis == 0L) return ""
    val sdf = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale("vi", "VN"))
    return sdf.format(Date(millis))
}