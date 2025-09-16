package com.edufelip.finn.shared.util.format
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

actual fun formatJoined(millis: Long): String {
    val fmt = SimpleDateFormat("LLLL yyyy", Locale.getDefault())
    return fmt.format(Date(millis))
}

actual fun formatRelative(millis: Long): String {
    val now = System.currentTimeMillis()
    val diff = (now - millis).coerceAtLeast(0)
    val sec = diff / 1000
    val min = sec / 60
    val hr = min / 60
    val day = hr / 24
    val week = day / 7
    val month = day / 30
    val year = day / 365
    return when {
        sec < 60 -> "just now"
        min < 60 -> "${min}m ago"
        hr < 24 -> "${hr}h ago"
        day < 7 -> "${day}d ago"
        week < 5 -> "${week}w ago"
        month < 12 -> "${month}mo ago"
        year < 100 -> "${year}y ago"
        else -> SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(millis))
    }
}
