package com.moor.scannit.ui.home

import android.util.SparseArray
import android.view.*
import android.widget.AdapterView
import androidx.core.util.containsKey
import androidx.core.util.set
import androidx.recyclerview.widget.RecyclerView
import com.moor.scannit.R
import com.moor.scannit.data.Folder
import com.moor.scannit.databinding.ItemFolderBinding
import com.moor.scannit.inflater
import com.moor.scannit.toDateString
import com.moor.scannit.ui.BoundViewHolder
import com.moor.scannit.ui.EditableAdapter
import kotlin.random.Random

import kotlin.random.Random.Default.nextInt

class FolderAdapter(val folders:List<Folder>):EditableAdapter<BoundViewHolder<ItemFolderBinding>>() {

    interface  FolderAdapterCallback{
       fun  onLongClick(folder:Folder,view:View)
    }
    var selected =  SparseArray<Folder>()
    var listener:FolderAdapterCallback?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):BoundViewHolder<ItemFolderBinding> {
        return BoundViewHolder(ItemFolderBinding.inflate(parent.inflater(),parent,false))
    }

    override fun getItemCount(): Int {
       return  folders.size
    }

    override fun onBindViewHolder(holder: BoundViewHolder<ItemFolderBinding>, position: Int) {
       holder.binding.apply {
           val folder= folders[position]
           nameTextView.text=folder.name
           countTextView.text= "${6}"
           createDateTextView.text=folder.create_date.toDateString()
//           if(selected.containsKey(position)){
//               root.setBackgroundResource(R.color.blue_700)
//           }
           root.setOnLongClickListener {view->
               listener?.onLongClick(folder,view)
//               selected.set(position,folder)
//               canEdit=true
               true
           }
//           root.setOnClickListener {
//               if (canEdit){
//                   if (selected.containsKey(position)){
//                       selected.remove(position)
//                   }else{
//                       selected.set(position,folder)
//                   }
//                   notifyItemChanged(position)
//               }
//           }
       }

    }

   // interface Folder

}