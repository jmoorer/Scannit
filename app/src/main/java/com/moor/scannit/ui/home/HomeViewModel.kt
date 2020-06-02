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

    private  val params = MutableLiveData(SearchParams())

    enum class  SortOrder{
        ASCENDING_NAME,
        DESCENDING_NAME,
        ASCENDING_DATE,
        DESCENDING_DATE
    }
    data class  SearchParams(val query: String="",val sortOrder: SortOrder?=null)

    fun getDocuments() = Transformations.switchMap(params){sp->

        val query= documentBox.query()
            .filter {  sp.query.isBlank()||it.name.contains(sp.query,true) }
            .sort(when(sp.sortOrder){
                SortOrder.ASCENDING_NAME-> compareBy { it.name }
                SortOrder.DESCENDING_NAME-> compareByDescending { it.name }
                SortOrder.ASCENDING_DATE -> compareBy { it.createDate }
                SortOrder.DESCENDING_DATE -> compareByDescending { it.createDate }
                else -> compareBy { it.id }
            }).build()
            return@switchMap ObjectBoxLiveData(query)
    }

    fun deleteDocuments(ids:LongArray){
        documentBox.remove(*ids)
    }

    fun filterDocuments(q:String){
        params.postValue(params.value?.copy(query = q))
    }

    fun sortDocuments(order: SortOrder){
        params.postValue(params.value?.copy(sortOrder = order))
    }

}