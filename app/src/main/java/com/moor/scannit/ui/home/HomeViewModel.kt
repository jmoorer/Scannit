package com.moor.scannit.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moor.scannit.data.Document
import com.moor.scannit.data.Document_
import com.moor.scannit.data.Folder
import com.moor.scannit.data.ObjectBox
import io.objectbox.Box
import io.objectbox.android.AndroidScheduler
import io.objectbox.kotlin.boxFor
import io.objectbox.query.Query

class HomeViewModel:ViewModel() {

    private  val documentBox:Box<Document> = ObjectBox.boxStore.boxFor()

    private var state= MutableLiveData<HomeState?>()

    private  var query:Query<Document>


    data class HomeState(
        val loading:Boolean=false,
        val documents:List<Document>,
        var currentFolderId:Long?=null
    )
    init {
        state.value = HomeState(documents = emptyList())
        query = documentBox.query().eager(Document_.pages).build().also {
            it.subscribe().on(AndroidScheduler.mainThread()).observer {documents->
                state.postValue(state.value?.copy(documents = documents))
            }
        }
    }

    fun getState():LiveData<HomeState?> = state

    fun saveDocument(document: Document){
        documentBox.put(document)
    }

    fun deleteDocument(document: Document){
        documentBox.remove(document)
    }
    fun deleteDocuments(ids:LongArray){
        documentBox.remove(*ids)
    }






}