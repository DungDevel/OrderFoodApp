package com.example.orderfood.fake

import com.example.orderfood.data.model.Food

object TestData {
    val phoBo = Food(1, "Phở bò", 45000, "url1", category = "Món chính", available = true)
    val comTam = Food(2, "Cơm tấm sườn", 40000, "url2", category = "Món chính", available = true)
    val traDaHetHang = Food(3, "Trà đá", 5000, "url3", category = "Đồ uống", available = false)
}