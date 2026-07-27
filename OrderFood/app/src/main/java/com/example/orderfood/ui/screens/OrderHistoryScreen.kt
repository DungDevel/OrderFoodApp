package com.example.orderfood.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.orderfood.data.model.OrderResponse
import com.example.orderfood.utils.toVnd
import com.example.orderfood.viewmodel.OrderHistoryUiState
import com.example.orderfood.viewmodel.OrderHistoryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    viewModel: OrderHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Lịch sử đơn hàng",
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp
                    )
                },
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {

                is OrderHistoryUiState.Loading ->
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )

                is OrderHistoryUiState.Error ->
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.message,
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(12.dp))

                        Button(onClick = { viewModel.loadOrders() }) {
                            Text("Thử lại")
                        }
                    }

                is OrderHistoryUiState.Empty ->
                    Text(
                        text = "Bạn chưa có đơn hàng nào",
                        modifier = Modifier.align(Alignment.Center)
                    )

                is OrderHistoryUiState.Success ->
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
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
}

@Composable
private fun OrderCard(order: OrderResponse) {
    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Đơn #${order.id.take(6).uppercase()}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(formatOrderDate(order.createdAt), style = MaterialTheme.typography.bodySmall, fontSize = 15.sp)
            }
            Spacer(Modifier.height(8.dp))
            order.items.forEach { item ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${item.name} x${item.quantity}", style = MaterialTheme.typography.bodyMedium, fontSize = 15.sp)
                    Text((item.price * item.quantity).toVnd(), style = MaterialTheme.typography.bodyMedium, fontSize = 15.sp)
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