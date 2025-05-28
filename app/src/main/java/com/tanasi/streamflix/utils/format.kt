package com.tanasi.streamflix.utils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
fun Calendar.format(pattern: String): String? {
    return try {
        val sdf = SimpleDateFormat(pattern, Locale.US)
        sdf.format(this.time)
    } catch (e: Exception) {
        null
    }
}
