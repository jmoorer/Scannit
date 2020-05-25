package com.moor.scannit


import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.media.Image
import android.net.Uri
import android.provider.MediaStore
import android.text.format.DateFormat
import android.text.format.DateFormat.format
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide

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
fun Fragment.loadBitmap(uri: Uri): Bitmap {
    val bitmap= MediaStore.Images.Media.getBitmap(requireContext().contentResolver,uri)
    val exif= ExifInterface(uri.path)
    val orientation: Int = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)
    val matrix = Matrix()
    when (orientation) {
        6 -> {
            matrix.postRotate(90f)
        }
        3 -> {
            matrix.postRotate(180f)
        }
        8 -> {
            matrix.postRotate(270f)
        }
    }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}
val Fragment.supportActionBar: ActionBar?
        get() = (activity as AppCompatActivity?)!!.supportActionBar


fun ImageView.load(uri: Uri){
    Glide.with(this).load(uri).into(this)
}

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

val Float.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()