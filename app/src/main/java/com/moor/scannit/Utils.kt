package com.moor.scannit

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

fun getOutputDirectory(context: Context): File {
    val appContext = context.applicationContext
    val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
        File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() }
    }
    return if (mediaDir != null && mediaDir.exists())mediaDir else appContext.filesDir
}

fun generateFileName(): String {
   return SimpleDateFormat(
        FILENAME_FORMAT, Locale.US
    ).format(System.currentTimeMillis())
}