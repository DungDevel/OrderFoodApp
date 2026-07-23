package com.example.orderfood.utils

import java.text.NumberFormat
import java.util.Locale
import java.text.Normalizer

fun Long.toVnd(): String =
    NumberFormat.getInstance(Locale("vi", "VN")).format(this) + " đ"
fun String.removeVietnameseDiacritics() : String{
    val normalized = Normalizer.normalize(this, Normalizer.Form.NFD)
    return normalized
        .replace(Regex("\\p{InCOMBINING_DIACRITICAL_MARKS}+"), "")
        .replace('đ', 'd')
        .replace('Đ', 'D')
}