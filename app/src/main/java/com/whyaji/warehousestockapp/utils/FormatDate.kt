package com.whyaji.warehousestockapp.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
fun timeMillisToFormattedString(timeMilis: String, format: String = "yyyy-MM-dd HH:mm:ss"): String {
    val time = timeMilis.toLong()
    val date = java.util.Date(time)
    val format = SimpleDateFormat(format)
    return format.format(date)
}