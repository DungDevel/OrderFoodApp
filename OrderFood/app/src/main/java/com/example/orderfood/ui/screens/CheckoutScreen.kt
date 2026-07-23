package com.example.orderfood.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.orderfood.utils.toVnd
import com.example.orderfood.viewmodel.CartViewModel
import com.example.orderfood.viewmodel.CheckoutState

@Composable
fun CheckoutScreen(
    cartViewModel: CartViewModel,
    onDone: () -> Unit
) {
    var showConfirmDialog by remember { mutableStateOf(true) }

    Box(Modifier.fillMaxSize()) {
        when (val state = cartViewModel.checkoutState) {
            is CheckoutState.Success -> Column(
                Modifier.align(Alignment.Center).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Gọi món thành công", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(12.dp))
                Button(onClick = {

                    cartViewModel.resetCheckoutState()
                    onDone()
                }) { Text("Tiếp tục gọi món") }
            }

            is CheckoutState.Loading -> Column(
                Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(Modifier.height(8.dp))
                Text("Đang xử lý")
            }

            is CheckoutState.Error -> Column(
                Modifier.align(Alignment.Center).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(state.message, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                Spacer(Modifier.height(12.dp))
                Button(onClick = { cartViewModel.checkout() }) { Text("Thử lại") }
            }

            is CheckoutState.Idle -> {
                if (showConfirmDialog) {
                    AlertDialog(
                        onDismissRequest = onDone,
                        title = { Text("Xác nhận gọi món") },
                        text = { Text("Thanh toán: ${cartViewModel.totalPrice.toVnd()}") },
                        confirmButton = {
                            TextButton(onClick = {
                                showConfirmDialog = false
                                cartViewModel.checkout()
                            }) { Text("Xác nhận") }
                        },
                        dismissButton = {
                            TextButton(onClick = onDone) { Text("Huỷ") }
                        }
                    )
                }
            }
        }
    }
}