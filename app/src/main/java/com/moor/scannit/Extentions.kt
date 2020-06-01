package com.moor.scannit


import android.app.ProgressDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.media.Image
import android.net.Uri
import android.provider.MediaStore
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.itextpdf.text.Document
import com.itextpdf.text.PageSize
import com.itextpdf.text.pdf.PdfWriter
import com.moor.scannit.data.Page
import java.io.File
import java.io.FileOutputStream
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

fun Context.getRealPathFromUri(contentUri: Uri): String? {
    var cursor: Cursor? = null
    return try {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        cursor = contentResolver?.query(contentUri, proj, null, null, null)
        assert(cursor != null)
        val column_index = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        cursor?.getString(column_index!!)
    } finally {
        cursor?.close()
    }
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

val Context.exportFolder
    get() = File(filesDir,"exports").apply {
        if(!exists())mkdirs();
    }
val Context.scanFolder
    get() = File(filesDir,"scans").apply { mkdir() }


fun Uri.rotation(): Float {
    val exif= ExifInterface(path)
    return when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)) {
        6 -> 90f
        3 -> 180f
        8 -> 270f
        else -> 0f
    }
}
fun Context.loadBitmap(uri: Uri): Bitmap {
    val bitmap= MediaStore.Images.Media.getBitmap(contentResolver,uri)
    val degrees= uri.rotation()
    val matrix = Matrix()
    matrix.postRotate(degrees)
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

fun Fragment.createProgressDialog(message:String): ProgressDialog {
    var progressDialog = ProgressDialog(activity);
    progressDialog.setTitle("My Content");
    progressDialog.setMessage("Loading this Content, please wait!");
    return progressDialog
}

fun Context.generatePdf(pages:List<Page>, name:String): File {

    try {
        val file = File(exportFolder,name)
        val doc = Document()
        val writer=PdfWriter.getInstance(doc, FileOutputStream(file))

        doc.pageSize = PageSize.A4;
        doc.open()

        pages.forEach { page->
            doc.newPage()
            val indentation = 0
            val image=com.itextpdf.text.Image.getInstance(page.uri)
            val scalar: Float = (doc.pageSize.width - doc.leftMargin() - doc.rightMargin() - indentation) / image.width * 100
            image.scalePercent(scalar)
            doc.add(image);
        }
        doc.close();
        writer.close();
        return file
    }
    catch (e: Exception){
        throw e
    }

}


fun Context.setClipboard(text: String) {
        val clipboard = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", text)
        clipboard.setPrimaryClip(clip)
}