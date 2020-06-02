package com.moor.scannit.ui.document

import android.os.Handler
import androidx.lifecycle.*
import com.moor.scannit.data.*
import io.objectbox.Box
import io.objectbox.android.ObjectBoxLiveData
import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.query

class DocumentViewModel:ViewModel() {
    private val document: MediatorLiveData<Document> = MediatorLiveData()
    private  val pages :MediatorLiveData<List<Page>> = MediatorLiveData()
    private  val documentBox: Box<Document> = ObjectBox.boxStore.boxFor()
    private  val pageBox :Box<Page> = ObjectBox.boxStore.boxFor()


    private var pageQuery:ObjectBoxLiveData<Page>?=null
    private var documentQuery:ObjectBoxLiveData<Document>?=null


    fun loadDocument(id:Long) {

        documentQuery?.let {document.removeSource(it)}
        pageQuery?.let { pages.removeSource(it) }

        documentQuery=ObjectBoxLiveData(documentBox.query { equal(Document_.id,id)})
        pageQuery=ObjectBoxLiveData( pageBox.query{equal(Page_.documentId,id).order(Page_.number)})

        document.addSource(documentQuery!!){
            document.postValue(it.first())
        }

        pages.addSource(pageQuery!!){
            pages.postValue(it)
        }

    }

    fun getDocument():LiveData<Document> = document
    fun getPages():LiveData<List<Page>> = pages

    fun saveDocument(document: Document) {
        documentBox.put(document)
    }
    fun removePage(page: Page){
        document.value?.let { doc->
            doc.pages.remove(page)
            reIndexPages(doc.pages)
            documentBox.put(doc)
        }
    }

    fun reIndexPages(pages:List<Page>){
        pages.forEachIndexed{ i, p->
            p.number=i+1
            pageBox.put(p)
        }
    }
}