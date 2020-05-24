package com.moor.scannit.ui.camera

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.moor.scannit.FILENAME_FORMAT
import com.moor.scannit.R
import com.moor.scannit.data.Document
import com.moor.scannit.data.ObjectBox
import com.moor.scannit.data.Page
import com.moor.scannit.generateFileName
import com.moor.scannit.getOutputDirectory
import io.objectbox.Box
import io.objectbox.BoxStore.context
import io.objectbox.kotlin.boxFor
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class CameraViewModel(application: Application) : AndroidViewModel(application) {
    val context:Context = application.applicationContext
    private  val documentBox:Box<Document> = ObjectBox.boxStore.boxFor()
    private  val pageBox:Box<Page> = ObjectBox.boxStore.boxFor()
    private  val originalImage=MutableLiveData<Bitmap>()
    private  val currentDocument = MutableLiveData<Document>()


    fun setImage(bitmap: Bitmap){
        originalImage.postValue(bitmap)
    }

    fun saveCroppedImage(bitmap: Bitmap): Long {

        val photoFile = File(
            getOutputDirectory(context),
            generateFileName() + ".jpg")

        val output = FileOutputStream(photoFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, output)
        output.flush()
        output.close()

        val doc=currentDocument.value!!
        val page=Page().apply {
            number= doc.pages.size+1
            uri = Uri.fromFile(photoFile).toString()
            rawText = ""
        }
        doc.pages.add(page)
        return documentBox.put(doc)

    }

    fun getImage() = originalImage

    fun setDocument(documentId:Long){
        val document = when(documentId){
                0L-> Document().apply {
                    name = generateFileName()
                    createDate = Date()
                }
                else->documentBox.get(documentId)
        }
        currentDocument.postValue(document)
    }

    fun saveDocument(document: Document){
        documentBox.put(document)
    }


}