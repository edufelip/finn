package com.edufelip.finn.shared.util.format
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale

actual fun formatJoined(millis: Long): String {
    val date = NSDate.dateWithTimeIntervalSince1970(millis.toDouble() / 1000.0)
    val formatter = NSDateFormatter()
    formatter.locale = NSLocale.currentLocale
    formatter.dateFormat = "LLLL yyyy"
    return formatter.stringFromDate(date)
}

actual fun formatRelative(millis: Long): String {
    val now = NSDate.date()
    val date = NSDate.dateWithTimeIntervalSince1970(millis.toDouble() / 1000.0)
    val diff = now.timeIntervalSinceDate(date)
    val sec = kotlin.math.max(0.0, diff)
    val min = sec / 60.0
    val hr = min / 60.0
    val day = hr / 24.0
    val week = day / 7.0
    val month = day / 30.0
    val year = day / 365.0
    return when {
        sec < 60 -> "just now"
        min < 60 -> "${min.toInt()}m ago"
        hr < 24 -> "${hr.toInt()}h ago"
        day < 7 -> "${day.toInt()}d ago"
        week < 5 -> "${week.toInt()}w ago"
        month < 12 -> "${month.toInt()}mo ago"
        year < 100 -> "${year.toInt()}y ago"
        else -> {
            val f = NSDateFormatter()
            f.locale = NSLocale.currentLocale
            f.dateFormat = "MMM d, yyyy"
            f.stringFromDate(date)
        }
    }
}
