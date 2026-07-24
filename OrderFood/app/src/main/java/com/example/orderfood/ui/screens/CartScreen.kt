package com.example.orderfood.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.orderfood.data.model.CartItem
import com.example.orderfood.utils.toVnd
import com.example.orderfood.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartViewModel: CartViewModel,
    onBack: () -> Unit,
    onCheckout: () -> Unit
) {
    var confirmRemoveId by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Giỏ hàng", fontWeight = FontWeight.Bold, fontSize = 30.sp)},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại", modifier = Modifier.size(25.dp))
                    }
                }
            )
        },
        bottomBar = {
            if (cartViewModel.items.isNotEmpty()) {
                Surface(shadowElevation = 8.dp) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Tổng cộng", style = MaterialTheme.typography.titleMedium, fontSize = 25.sp)
                            Text(cartViewModel.totalPrice.toVnd(), style = MaterialTheme.typography.titleMedium, fontSize = 25.sp)
                        }
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = onCheckout, modifier = Modifier.fillMaxWidth()) {
                            Text(text = "Thanh toán", fontSize = 25.sp)
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            if (cartViewModel.items.isEmpty()) {
                Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Giỏ hàng trống", fontWeight = FontWeight.Bold, fontSize = 25.sp)
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = onBack) { Text(text = "Quay lại đặt món", fontWeight = FontWeight.Bold, fontSize = 20.sp) }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cartViewModel.items, key = { it.food.id }) { item ->
                        CartItemRow(
                            item = item,
                            onIncrease = { cartViewModel.increase(item.food.id) },
                            onDecrease = {
                                if (item.quantity == 1) confirmRemoveId = item.food.id
                                else cartViewModel.decrease(item.food.id)
                            }
                        )
                    }
                }
            }
        }
    }

    if (confirmRemoveId != null) {
        AlertDialog(
            onDismissRequest = { confirmRemoveId = null },
            title = { Text("Xoá món") },
            confirmButton = {
                TextButton(onClick = {
                    cartViewModel.removeItem(confirmRemoveId!!)
                    confirmRemoveId = null
                }) { Text("Xoá") }
            },
            dismissButton = {
                TextButton(onClick = { confirmRemoveId = null }) { Text("Huỷ") }
            }
        )
    }
}

@Composable
private fun CartItemRow(item: CartItem, onIncrease: () -> Unit, onDecrease: () -> Unit) {
    ElevatedCard(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(item.food.name, style = MaterialTheme.typography.titleSmall, fontSize = 25.sp)
                Text(item.food.price.toVnd(), style = MaterialTheme.typography.bodySmall, fontSize = 20.sp)
            }
            IconButton(onClick = onDecrease) { Text(text = "−", fontSize = 30.sp) }
            Text("${item.quantity}", Modifier.padding(horizontal = 6.dp), fontSize = 20.sp)
            IconButton(onClick = onIncrease) { Text("+", fontSize = 30.sp) }
            Spacer(Modifier.width(8.dp))
            Text(item.totalPrice.toVnd(), style = MaterialTheme.typography.titleSmall, fontSize = 20.sp)
        }
    }
}