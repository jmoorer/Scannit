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

    enum class  SortOrder{
        ASCENDING_NAME,
        DESCENDING_NAME,
        ASCENDING_DATE,
        DESCENDING_DATE
    }

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

    fun sortDocuments(order: SortOrder){
        val docs=documentBox.query().sort(when(order){
                SortOrder.ASCENDING_NAME-> compareBy { it.name }
                SortOrder.DESCENDING_NAME-> compareByDescending { it.name }
                SortOrder.ASCENDING_DATE -> compareBy { it.createDate }
                SortOrder.DESCENDING_DATE -> compareByDescending { it.createDate }
        }).build().find()
        state.postValue(state.value?.copy(documents = docs))
    }





}