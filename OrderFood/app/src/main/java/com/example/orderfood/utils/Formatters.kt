package com.example.orderfood.utils

import java.text.NumberFormat
import java.util.Locale

fun Long.toVnd(): String =
    NumberFormat.getInstance(Locale("vi", "VN")).format(this) + " đ"