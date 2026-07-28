package com.example.orderfood.viewmodel

import android.content.SharedPreferences
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class FavoriteViewModelTest {

    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var storedSet: Set<String> = emptySet()

    @Before
    fun setup() {
        prefs = mockk(relaxed = true)
        editor = mockk(relaxed = true)

        every { prefs.getStringSet(any(), any()) } answers { storedSet }
        every { prefs.edit() } returns editor
        val slot = slot<Set<String>>()
        every { editor.putStringSet(any(), capture(slot)) } answers {
            storedSet = slot.captured
            editor
        }
    }

    @Test
    fun `them mon vao yeu thich`() {
        val viewModel = FavoriteViewModel(prefs)
        viewModel.toggleFavorite(1)

        assertTrue(viewModel.isFavorite(1))
    }

    @Test
    fun `bam lan 2 se khong thich`() {
        val viewModel = FavoriteViewModel(prefs)
        viewModel.toggleFavorite(1)
        viewModel.toggleFavorite(1)

        assertFalse(viewModel.isFavorite(1))
    }

    @Test
    fun `mon chua thich thi isFavorite tra ve false`() {
        val viewModel = FavoriteViewModel(prefs)
        assertFalse(viewModel.isFavorite(999))
    }
}