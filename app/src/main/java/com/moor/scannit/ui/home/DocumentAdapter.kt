package com.moor.scannit.ui.home

import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.moor.scannit.data.Document
import com.moor.scannit.databinding.ItemDocumentBinding
import com.moor.scannit.inflater
import com.moor.scannit.toDateString
import com.moor.scannit.ui.BoundViewHolder
import com.moor.scannit.ui.EditableAdapter

class DocumentAdapter(val documents:List<Document>):RecyclerView.Adapter<BoundViewHolder<ItemDocumentBinding>>() {

    interface  FolderAdapterCallback{
        fun  onLongClick(document: Document,view:View)
        fun onClick(document: Document,view:View)
    }

    var listener:FolderAdapterCallback?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):BoundViewHolder<ItemDocumentBinding> {
        return BoundViewHolder(ItemDocumentBinding.inflate(parent.inflater(),parent,false))
    }

    override fun getItemCount(): Int {
       return  documents.size
    }

    override fun onBindViewHolder(holder: BoundViewHolder<ItemDocumentBinding>, position: Int) {
       holder.binding.apply {
           val document= documents[position]
           nameTextView.text=document.name
           countTextView.text= "${document.pages.count()}"
           createDateTextView.text=document.createDate?.toDateString()

           root.setOnLongClickListener {view->
               listener?.onLongClick(document,view)
               true
           }
           root.setOnClickListener {view->
               listener?.onClick(document,view)
           }

       }

    }

}