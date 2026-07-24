package com.example.orderfood.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.orderfood.viewmodel.CartViewModel
import com.example.orderfood.viewmodel.FavoriteViewModel
import com.example.orderfood.viewmodel.FoodUiState
import com.example.orderfood.viewmodel.FoodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    foodViewModel: FoodViewModel,
    cartViewModel: CartViewModel,
    favoriteViewModel: FavoriteViewModel = hiltViewModel(),
    onCartClick: () -> Unit
) {
    val uiState by foodViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                        Text(text = "Yêu thích", fontWeight = FontWeight.Bold, fontSize = 30.sp)
                },
                actions = {
                        BadgedBox(
                            badge = {
                                if (cartViewModel.totalQuantity > 0)
                                    Badge { Text(text = "${cartViewModel.totalQuantity}", fontSize = 15.sp) }
                            },
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            IconButton(onClick = onCartClick) {
                                Icon(
                                    Icons.Default.ShoppingCart,
                                    contentDescription = "Giỏ hàng",
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                }
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            when (val state = uiState) {

                is FoodUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is FoodUiState.Success -> {

                    val favoriteFoods = state.foods.filter {
                        favoriteViewModel.favoriteIds.contains(it.id)
                    }

                    if (favoriteFoods.isEmpty()) {

                        Text(
                            text = "Chưa có món ăn yêu thích",
                            modifier = Modifier.align(Alignment.Center),
                            textAlign = TextAlign.Center
                        )

                    } else {

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {

                            items(
                                items = favoriteFoods,
                                key = { it.id }
                            ) { food ->

                                FoodItemCard(
                                    food = food,
                                    isFavorite = true,
                                    onAdd = {
                                        cartViewModel.addToCart(food)
                                    },
                                    onToggleFavorite = {
                                        favoriteViewModel.toggleFavorite(food.id)
                                    }
                                )
                            }
                        }
                    }
                }

                is FoodUiState.Error -> {
                    Text(
                        text = state.message,
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center
                    )
                }

                is FoodUiState.Empty -> {
                    Text(
                        text = "Không có dữ liệu",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}