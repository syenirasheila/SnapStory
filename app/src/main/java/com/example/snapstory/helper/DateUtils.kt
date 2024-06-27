package com.example.snapstory.helper

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun String.formatToString(): String? {
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    return try {
        val date: Date? = format.parse(this)
        if (date != null) {
            DateFormat.getDateInstance(DateFormat.FULL).format(date)
        } else {
            null
        }
    } catch (e: ParseException) {
        e.printStackTrace()
        null
    }
}

private const val DATE_FORMAT = "dd-MMM-yyyy"
val timeStamp: String = SimpleDateFormat(DATE_FORMAT, Locale.US).format(System.currentTimeMillis())
