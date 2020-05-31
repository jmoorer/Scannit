package com.moor.scannit.ui.document

import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moor.scannit.data.Page
import com.moor.scannit.databinding.ItemPageBinding
import com.moor.scannit.databinding.ItemPageViewBinding
import com.moor.scannit.inflater
import com.moor.scannit.load
import com.moor.scannit.ui.BoundViewHolder


class PageViewAdapter(val pages:List<Page>):RecyclerView.Adapter<BoundViewHolder<ItemPageViewBinding>>() {

    interface  PageAdapterCallback{
        fun  onLongClick(page: Page, view: View)
        fun onClick(page: Page, view: View)
    }

    var listener:PageAdapterCallback?=null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BoundViewHolder<ItemPageViewBinding> {

        val itemView=ItemPageViewBinding.inflate(parent.inflater(),parent,false)
        return  BoundViewHolder(itemView)
    }

    override fun getItemCount()= pages.size

    override fun onBindViewHolder(holder: BoundViewHolder<ItemPageViewBinding>, position: Int) {
          val page= pages[position]
          holder.binding.apply {
              previewImageView.load(Uri.parse(page.uri))
              root.setOnClickListener {v-> listener?.onClick(page,v) }
          }
    }
}