package com.moor.scannit.ui.home

import android.view.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.moor.scannit.data.Document
import com.moor.scannit.databinding.ItemDocumentBinding
import com.moor.scannit.inflater
import com.moor.scannit.toDateString
import com.moor.scannit.ui.AdapterCallback
import com.moor.scannit.ui.BoundViewHolder

class DocumentAdapter(val documents:List<Document>):RecyclerView.Adapter<BoundViewHolder<ItemDocumentBinding>>() {

    var selectedIds= mutableSetOf<Long>()
    var canEdit:Boolean = false
        set(value) {
            field=value
            notifyDataSetChanged()
        }

    var listener: AdapterCallback<Document>?=null

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
           selectedCheckbox.isVisible= canEdit

           if (canEdit){
               selectedCheckbox.isChecked =selectedIds.contains(document.id)
               selectedCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
                   if (isChecked)selectedIds.add(document.id) else selectedIds.remove(document.id)
               }
               root.setOnClickListener{
                   selectedCheckbox.toggle()
               }
           }else{
               root.setOnClickListener {view->
                   listener?.onClick(document,view)
               }
           }
           root.setOnLongClickListener {view->
               listener?.onLongClick(document,view)
               true
           }


       }

    }

}