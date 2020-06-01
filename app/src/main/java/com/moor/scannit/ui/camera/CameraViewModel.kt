package com.moor.scannit.ui.camera

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.*
import com.moor.scannit.*
import com.moor.scannit.data.Document
import com.moor.scannit.data.ObjectBox
import com.moor.scannit.data.Page
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

    private val current get() = state.value

    private val initialState=CameraModelState(
        document = Document().apply {
            name="Document"
            createDate= Date()
        },
        batchBitmaps = emptyList(),
        cameraMode = CameraMode.Single
    )
    private val state = MutableLiveData(initialState)

    val mode get() = current?.cameraMode?:CameraMode.None

    fun getState():LiveData<CameraModelState> = state

    data class CameraModelState(
        val loading:Boolean=false,
        val document:Document,
        val originalBitmap: Bitmap?=null,
        val batchBitmaps: List<Bitmap>,
        val cameraMode: CameraMode
    )


    enum class CameraMode{
        Single,
        Ocr,
        Batch,
        None
    }

    fun setImage(bitmap: Bitmap){
       state.postValue(current?.copy(originalBitmap = bitmap))
    }


    fun addBatchImage(bitmap: Bitmap){
        current?.batchBitmaps?.let {bitmaps->
            state.postValue(current?.copy(batchBitmaps = bitmaps.plus(bitmap)))
        }
    }
    fun setMode(mode: CameraMode){
        state.postValue(current?.copy(cameraMode = mode))
    }

    fun saveCroppedImage(bitmap: Bitmap): Long {

        val photoFile = File(context.scanFolder, generateFileName() + ".jpg")

        val output = FileOutputStream(photoFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output)
        output.flush()
        output.close()

        val doc=current!!.document
        val page=Page().apply {
            number= doc.pages.size+1
            uri = Uri.fromFile(photoFile).toString()
            rawText = ""
        }
        doc.pages.add(page)
        state.postValue(initialState)
        return documentBox.put(doc)

    }

    fun saveBatch(): Long {
        current?.document?.let {doc->
            current?.batchBitmaps?.forEach { bitmap->
                val photoFile = File(context.scanFolder, "${generateFileName()}.jpg")
                val output = FileOutputStream(photoFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output)
                output.flush()
                output.close()
                val page=Page().apply {
                    number= doc.pages.size+1
                    uri = Uri.fromFile(photoFile).toString()
                    rawText = ""
                }
                doc.pages.add(page)
            }
            state.postValue(initialState)
            return documentBox.put(doc)
        }
        return -1
    }

    fun getImage() = Transformations.map(state){s->
        s.originalBitmap
    }

    fun setDocument(documentId:Long){
        val document = when(documentId){
                0L-> Document().apply {
                    name = "Document"
                    createDate = Date()
                }
                else->documentBox.get(documentId)
        }
        state.value=current?.copy(document=document)
    }






}