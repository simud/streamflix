package com.tanasi.streamflix.utils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
fun String.toCalendar(pattern: String = "yyyy-MM-dd"): Calendar? {
    return try {
        val sdf = SimpleDateFormat(pattern, Locale.US)
        val date = sdf.parse(this)
        Calendar.getInstance().apply { time = date }
    } catch (e: Exception) {
        null
    }
}
