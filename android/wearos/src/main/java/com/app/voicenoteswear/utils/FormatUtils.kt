package com.app.voicenoteswear.utils

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatDateString(dateString: String) : String {
    val zonedDateTime = ZonedDateTime.parse(dateString)
    val formatter = DateTimeFormatter.ofPattern("MMMM dd", Locale.ENGLISH)
    return zonedDateTime.format(formatter)
}

fun formatDurationString(milliseconds: Long) : String {
    val seconds = milliseconds / 1000
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}