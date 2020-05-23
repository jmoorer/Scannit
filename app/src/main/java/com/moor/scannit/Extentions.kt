package com.moor.scannit

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.Image
import android.text.format.DateFormat
import android.text.format.DateFormat.format
import android.view.LayoutInflater
import android.view.ViewGroup

import java.util.*


fun Image.toBitmap(rotationDegrees: Int): Bitmap {

    val buffer = planes[0].buffer
    buffer.rewind()
    val bytes = ByteArray(buffer.capacity())
    buffer.get(bytes)
    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    val matrix = Matrix()
    matrix.postRotate(rotationDegrees.toFloat())
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

fun ViewGroup.inflater(): LayoutInflater {
    return LayoutInflater.from(this.context)
}
fun Date.toDateString(): String {
     return DateFormat.format("MM/dd/yyyy", this).toString()
}