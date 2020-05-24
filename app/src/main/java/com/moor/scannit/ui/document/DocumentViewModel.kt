package com.moor.scannit.ui.document

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.moor.scannit.data.Document
import com.moor.scannit.data.Document_
import com.moor.scannit.data.ObjectBox
import io.objectbox.Box
import io.objectbox.android.ObjectBoxLiveData
import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.query

class DocumentViewModel:ViewModel() {
    private  val documentBox: Box<Document> = ObjectBox.boxStore.boxFor()


    fun loadDocument(id:Long): LiveData<Document> {
        return Transformations.map(ObjectBoxLiveData(documentBox.query { equal(Document_.id,id) })){
          it.first()
        }
    }
}