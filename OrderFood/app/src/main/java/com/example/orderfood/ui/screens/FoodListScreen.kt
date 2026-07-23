package com.example.orderfood.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.orderfood.data.model.Food
import com.example.orderfood.utils.toVnd
import com.example.orderfood.viewmodel.CartViewModel
import com.example.orderfood.viewmodel.FoodUiState
import com.example.orderfood.viewmodel.FoodViewModel
import androidx.compose.foundation.ExperimentalFoundationApi

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FoodListScreen(
    foodViewModel: FoodViewModel,
    cartViewModel: CartViewModel,
    onCartClick: () -> Unit
) {
    val uiState by foodViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Thực đơn", fontWeight = FontWeight.Bold, fontSize = 30.sp) },
                actions = {
                    BadgedBox(
                        badge = {
                            if (cartViewModel.totalQuantity > 0)
                                Badge { Text("${cartViewModel.totalQuantity}") }
                        },
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        IconButton(onClick = onCartClick) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Giỏ hàng", modifier = Modifier.size(40.dp))
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is FoodUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                is FoodUiState.Error -> Column(
                    Modifier.align(Alignment.Center).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(state.message, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { foodViewModel.loadFoods() }) { Text("Thử lại") }
                }

                is FoodUiState.Empty -> Text(
                    "Hiện chưa có món ăn nào",
                    Modifier.align(Alignment.Center)
                )

                is FoodUiState.Success -> {
                    val groupedFoods = remember(state.foods) {
                        state.foods.groupBy { it.category.ifBlank { "Khác" } }
                    }

                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        groupedFoods.forEach { (category, foodsInCategory) ->
                            stickyHeader {
                                CategoryHeader(category)
                            }
                            items(foodsInCategory, key = { it.id }) { food ->
                                Box(Modifier.padding(horizontal = 12.dp, vertical = 5.dp)) {
                                    FoodItemCard(food = food, onAdd = { cartViewModel.addToCart(food) })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FoodItemCard(food: Food, onAdd: () -> Unit) {
    ElevatedCard(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = food.image,
                contentDescription = food.name,
                modifier = Modifier.size(100.dp),
                error = androidx.compose.ui.graphics.painter.ColorPainter(
                    androidx.compose.ui.graphics.Color.LightGray
                )
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(food.name, style = MaterialTheme.typography.titleMedium, fontSize = 20.sp)
                Text(food.price.toVnd(), style = MaterialTheme.typography.bodyMedium)
                if (!food.available) {
                    Text("Hết món", color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall)
                }
            }
            Button(onClick = onAdd, enabled = food.available) { Text(text = "Thêm món", fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
private fun CategoryHeader(category: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = category,
            style = MaterialTheme.typography.titleMedium,
            fontSize = 20.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}