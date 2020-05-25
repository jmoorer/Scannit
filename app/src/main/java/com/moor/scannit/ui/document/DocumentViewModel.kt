package com.moor.scannit.ui.document

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.moor.scannit.data.Document
import com.moor.scannit.data.Document_
import com.moor.scannit.data.ObjectBox
import com.moor.scannit.data.Page
import io.objectbox.Box
import io.objectbox.android.ObjectBoxLiveData
import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.query

class DocumentViewModel:ViewModel() {
    private val document: MutableLiveData<Document> = MutableLiveData()
    private  val documentBox: Box<Document> = ObjectBox.boxStore.boxFor()


    fun loadDocument(id:Long) {
        documentBox.query { equal(Document_.id,id) }.subscribe().observer {
            document.postValue(it.first())
        }
    }

    fun getDocument():LiveData<Document> = document

    fun saveDocument(document: Document) {
        documentBox.put(document)
    }
    fun removePage(page: Page){
        document.value?.let { doc->
            doc.pages.apply {
                remove(page)
                forEachIndexed{ i, p->
                    p.number=i+1
                }
                documentBox.put(doc)
            }
        }


    }
}