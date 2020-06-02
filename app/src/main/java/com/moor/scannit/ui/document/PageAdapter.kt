package com.moor.scannit.ui.document

import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.moor.scannit.data.Page
import com.moor.scannit.databinding.ItemPageBinding
import com.moor.scannit.inflater
import com.moor.scannit.load
import com.moor.scannit.ui.AdapterCallback
import com.moor.scannit.ui.BoundViewHolder


class PageAdapter(val pages:List<Page>):RecyclerView.Adapter<BoundViewHolder<ItemPageBinding>>() {



    var selectedIds= mutableSetOf<Long>()
    var canEdit:Boolean = false
        set(value) {
            field=value
            notifyDataSetChanged()
        }

    var listener:AdapterCallback<Page>?=null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BoundViewHolder<ItemPageBinding> {

        val itemView=ItemPageBinding.inflate(parent.inflater(),parent,false)
        val layoutParams: ViewGroup.LayoutParams = itemView.root.layoutParams
        layoutParams.height = ((parent.height * 0.3).toInt())
        itemView.root.layoutParams = layoutParams
        return  BoundViewHolder(itemView)
    }

    override fun getItemCount()= pages.size

    override fun onBindViewHolder(holder: BoundViewHolder<ItemPageBinding>, position: Int) {
          val page= pages[position]
          holder.binding.apply {
              previewImageView.load(Uri.parse(page.uri))
              pageTextView.text="${page.number}"
              selectedCheckbox.isVisible= canEdit
              if (canEdit){
                  selectedCheckbox.isChecked =selectedIds.contains(page.id)
                  selectedCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
                      if (isChecked)selectedIds.add(page.id) else selectedIds.remove(page.id)
                  }
                  root.setOnClickListener{
                      selectedCheckbox.toggle()
                  }
              }else{
                  root.setOnClickListener {v-> listener?.onClick(page,v) }
              }


          }

    }
}