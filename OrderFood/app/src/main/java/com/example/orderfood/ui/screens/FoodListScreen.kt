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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import com.example.orderfood.utils.removeVietnameseDiacritics

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FoodListScreen(
    foodViewModel: FoodViewModel,
    cartViewModel: CartViewModel,
    onCartClick: () -> Unit
) {
    val uiState by foodViewModel.uiState.collectAsState()

    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isSearchActive) {
                        SearchTextField(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it },
                            onClose = {
                                isSearchActive = false
                                searchQuery = ""
                            }
                        )
                    } else {
                        Text(text = "Thực đơn", fontWeight = FontWeight.Bold, fontSize = 30.sp)
                    }
                },
                actions = {
                    if (!isSearchActive) {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Tìm kiếm", modifier = Modifier.size(28.dp))
                        }
                        BadgedBox(
                            badge = {
                                if (cartViewModel.totalQuantity > 0)
                                    Badge { Text("${cartViewModel.totalQuantity}") }
                            },
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            IconButton(onClick = onCartClick) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = "Giỏ hàng", modifier = Modifier.size(28.dp))
                            }
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
                    val filteredFoods = remember(state.foods, searchQuery) {
                        if (searchQuery.isBlank()) {
                            state.foods
                        } else {
                            val normalizedQuery = searchQuery.removeVietnameseDiacritics()
                            state.foods.filter { food ->
                                food.name.removeVietnameseDiacritics()
                                    .contains(normalizedQuery, ignoreCase = true)
                            }
                        }
                    }

                    if (filteredFoods.isEmpty()) {
                        Box(Modifier.fillMaxSize()) {
                            Text(
                                "Không tìm thấy món \"$searchQuery\"",
                                Modifier.align(Alignment.Center).padding(24.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    } else {
                        val groupedFoods = remember(filteredFoods) {
                            filteredFoods.groupBy { it.category.ifBlank { "Khác" } }
                        }

                        LazyColumn(
                            contentPadding = PaddingValues(bottom = 12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            groupedFoods.forEach { (category, foodsInCategory) ->
                                stickyHeader { CategoryHeader(category) }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTextField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        placeholder = { Text("Tìm món ăn...") },
        singleLine = true,
        trailingIcon = {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Đóng tìm kiếm")
            }
        },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        )
    )
}