package com.example.orderfood.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.orderfood.viewmodel.CartViewModel
import com.example.orderfood.viewmodel.FavoriteViewModel
import com.example.orderfood.viewmodel.FoodUiState
import com.example.orderfood.viewmodel.FoodViewModel

@Composable
fun FavoriteScreen(
    foodViewModel: FoodViewModel,
    cartViewModel: CartViewModel,
    favoriteViewModel: FavoriteViewModel = hiltViewModel()
) {
    val uiState by foodViewModel.uiState.collectAsState()

    Box(Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is FoodUiState.Success -> {
                val favoriteFoods = state.foods.filter {
                    favoriteViewModel.favoriteIds.contains(it.id)
                }
                if (favoriteFoods.isEmpty()) {
                    Text(
                        "Chưa có món ăn yêu thích",
                        Modifier.align(Alignment.Center).padding(24.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(favoriteFoods, key = { it.id }) { food ->
                            Box(Modifier.padding(horizontal = 0.dp, vertical = 5.dp)) {
                                FoodItemCard(
                                    food = food,
                                    isFavorite = true,
                                    onAdd = { cartViewModel.addToCart(food) },
                                    onToggleFavorite = { favoriteViewModel.toggleFavorite(food.id) }
                                )
                            }
                        }
                    }
                }
            }
            else -> CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    }
}