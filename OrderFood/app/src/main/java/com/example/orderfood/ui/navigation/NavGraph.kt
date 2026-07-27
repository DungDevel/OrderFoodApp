package com.example.orderfood.ui.navigation

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

object Routes {
    const val FOOD_LIST = "food_list"
    const val CART = "cart"
    const val CHECKOUT = "checkout"
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    val cartViewModel: CartViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = Routes.FOOD_LIST) {
        composable(Routes.FOOD_LIST) {
            val foodViewModel: FoodViewModel = hiltViewModel()
            FoodListScreen(
                foodViewModel = foodViewModel,
                cartViewModel = cartViewModel,
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
    }
}