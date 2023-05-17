package com.urwork.mobile.services

import android.webkit.MimeTypeMap
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun getMimeType(file: File): String? {
    val extension = MimeTypeMap.getFileExtensionFromUrl(file.path)
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
}

fun formatDate(date: String, format: String ) : String {
    var formattedDate = ""
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")

    try {
        val parseDate = sdf.parse(date)
        formattedDate = SimpleDateFormat(format).format(parseDate)
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    return formattedDate
}