package com.example.orderfood.viewmodel

import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val prefs: SharedPreferences
) : ViewModel() {

    companion object {
        private const val KEY_FAVORITES = "favorite_food_ids"
    }

    var favoriteIds by mutableStateOf<Set<Int>>(loadFavorites())
        private set

    private fun loadFavorites(): Set<Int> =
        prefs.getStringSet(KEY_FAVORITES, emptySet())
            ?.mapNotNull { it.toIntOrNull() }
            ?.toSet() ?: emptySet()

    fun toggleFavorite(foodId: Int) {
        val updated = if (favoriteIds.contains(foodId)) {
            favoriteIds - foodId
        } else {
            favoriteIds + foodId
        }
        favoriteIds = updated
        prefs.edit()
            .putStringSet(KEY_FAVORITES, updated.map { it.toString() }.toSet())
            .apply()
    }

    fun isFavorite(foodId: Int): Boolean = favoriteIds.contains(foodId)
}