package com.example.orderfood.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.orderfood.ui.screens.CartScreen
import com.example.orderfood.ui.screens.CheckoutScreen
import com.example.orderfood.ui.screens.FoodListScreen
import com.example.orderfood.viewmodel.CartViewModel
import com.example.orderfood.viewmodel.FoodViewModel
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.orderfood.ui.screens.FavoriteScreen
import com.example.orderfood.ui.screens.OrderHistoryScreen
import com.example.orderfood.viewmodel.FavoriteViewModel
import androidx.compose.runtime.getValue

object Routes {
    const val FOOD_LIST = "food_list"
    const val CART = "cart"
    const val CHECKOUT = "checkout"
    const val FAVORITE = "favorite"
    const val ORDER_HISTORY = "order_history"
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val cartViewModel: CartViewModel = hiltViewModel()

    val bottomNavRoutes = setOf(Routes.FOOD_LIST, Routes.FAVORITE, Routes.ORDER_HISTORY)
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomNavRoutes) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == Routes.FOOD_LIST,
                        onClick = {
                            navController.navigate(Routes.FOOD_LIST) {
                                popUpTo(Routes.FOOD_LIST) { inclusive = true }
                            }
                        },
                        icon = { Icon(Icons.Default.Restaurant, contentDescription = "Thực đơn") },
                        label = { Text("Thực đơn") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.FAVORITE,
                        onClick = {
                            navController.navigate(Routes.FAVORITE) {
                                popUpTo(Routes.FOOD_LIST)
                            }
                        },
                        icon = { Icon(Icons.Default.Favorite, contentDescription = "Yêu thích") },
                        label = { Text("Yêu thích") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.ORDER_HISTORY,
                        onClick = {
                            navController.navigate(Routes.ORDER_HISTORY) {
                                popUpTo(Routes.FOOD_LIST)
                            }
                        },
                        icon = { Icon(Icons.Default.History, contentDescription = "Lịch sử") },
                        label = { Text("Lịch sử") }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.FOOD_LIST,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.FOOD_LIST) {
                val foodViewModel: FoodViewModel = hiltViewModel()
                val favoriteViewModel: FavoriteViewModel = hiltViewModel()
                FoodListScreen(
                    foodViewModel = foodViewModel,
                    cartViewModel = cartViewModel,
                    favoriteViewModel = favoriteViewModel,
                    onCartClick = { navController.navigate(Routes.CART) }
                )
            }
            composable(Routes.CART) {
                CartScreen(
                    cartViewModel = cartViewModel,
                    onBack = { navController.popBackStack() },
                    onCheckout = { navController.navigate(Routes.CHECKOUT) }
                )
            }
            composable(Routes.CHECKOUT) {
                CheckoutScreen(
                    cartViewModel = cartViewModel,
                    onDone = { navController.popBackStack(Routes.FOOD_LIST, inclusive = false) }
                )
            }
            composable(Routes.FAVORITE) {
                val foodViewModel: FoodViewModel = hiltViewModel()
                FavoriteScreen(
                    foodViewModel = foodViewModel,
                    cartViewModel = cartViewModel
                )
            }
            composable(Routes.ORDER_HISTORY) {
                OrderHistoryScreen()
            }
        }
    }
}