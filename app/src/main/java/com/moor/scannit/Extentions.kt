package com.moor.scannit

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
    var bitmap= MediaStore.Images.Media.getBitmap(requireContext().contentResolver,uri)
    var exif= ExifInterface(uri.path)
    val orientation: Int = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)
    val matrix = Matrix()
    if (orientation == 6) {
        matrix.postRotate(90f)
    } else if (orientation == 3) {
        matrix.postRotate(180f)
    } else if (orientation == 8) {
        matrix.postRotate(270f)
    }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

fun ImageView.load(uri: Uri){
    Glide.with(this).load(uri).into(this)
}