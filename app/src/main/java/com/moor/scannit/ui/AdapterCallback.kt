package com.moor.scannit.ui

import android.view.View
import com.moor.scannit.data.Document

interface  AdapterCallback<T>{
    fun  onLongClick(item: T, view: View)
    fun onClick(item: T, view: View)
}