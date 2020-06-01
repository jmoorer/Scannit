package com.moor.scannit.ui.home

import androidx.lifecycle.*
import com.moor.scannit.data.Document
import com.moor.scannit.data.Document_
import com.moor.scannit.data.Folder
import com.moor.scannit.data.ObjectBox
import io.objectbox.Box
import io.objectbox.android.AndroidScheduler
import io.objectbox.android.ObjectBoxLiveData
import io.objectbox.kotlin.boxFor
import io.objectbox.query.Query

class HomeViewModel:ViewModel() {

    private  val documentBox:Box<Document> = ObjectBox.boxStore.boxFor()

    private var state= MediatorLiveData<HomeState?>()

    private  var query:Query<Document>


    data class HomeState(
        val loading:Boolean=false,
        val documents:List<Document>,
        var currentFolderId:Long?=null
    )
    init {
        state.value = HomeState(documents = emptyList())
        query = documentBox.query().eager(Document_.pages).build()
        state.addSource(ObjectBoxLiveData(query)){ documents->
            state.postValue(state.value?.copy(documents = documents))
        }
    }

    fun getState():LiveData<HomeState?> = state


    fun deleteDocuments(ids:LongArray){
        documentBox.remove(*ids)
    }

    fun filterDocuments(q:String){
        val filterQuery = documentBox.query().filter { q.isBlank()||it.name.contains(q,true) }.eager(Document_.pages).build()
        state.postValue(state.value?.copy(documents = filterQuery.find()))
    }





}