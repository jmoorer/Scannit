package com.moor.scannit.ui

import androidx.recyclerview.widget.RecyclerView

abstract class EditableAdapter<T>: RecyclerView.Adapter<T>() where T:RecyclerView.ViewHolder {

    protected  val EDITMODE=2
    protected  val VIEWMODE=1
   var canEdit=false
        get() = field
        set(value) {
           field=value
           notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int): Int {
        return if (canEdit) EDITMODE else VIEWMODE
    }


}