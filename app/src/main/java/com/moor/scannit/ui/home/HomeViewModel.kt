package com.moor.scannit.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moor.scannit.data.Folder
import com.moor.scannit.data.ObjectBox
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.objectbox.query.Query

class HomeViewModel:ViewModel() {

    private  val folderBox:Box<Folder> = ObjectBox.boxStore.boxFor()

    private var state= MutableLiveData<HomeState?>()

    private  var query:Query<Folder>
    data class HomeState(
        val loading:Boolean=false,
        val folders:List<Folder>
    )
    init {
        state.value = HomeState(folders = emptyList())
        query= folderBox.query().build().also {
            it.subscribe().observer { folders->
                state.postValue(state.value?.copy(folders = folders))
            }
        }
    }

    fun getState():LiveData<HomeState?> = state

    fun saveFolder(folder: Folder){
        folderBox.put(folder)
    }
    fun deleteFolder(folder:Folder){
        folderBox.remove(folder)
    }





}