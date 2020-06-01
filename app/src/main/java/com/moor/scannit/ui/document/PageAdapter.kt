package com.moor.scannit.ui.document

import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moor.scannit.data.Page
import com.moor.scannit.databinding.ItemPageBinding
import com.moor.scannit.inflater
import com.moor.scannit.load
import com.moor.scannit.ui.BoundViewHolder


class PageAdapter(val pages:List<Page>):RecyclerView.Adapter<BoundViewHolder<ItemPageBinding>>() {

    interface  PageAdapterCallback{
        fun  onLongClick(page: Page, view: View)
        fun onClick(page: Page, view: View)
    }

    var listener:PageAdapterCallback?=null
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
              root.setOnClickListener {v-> listener?.onClick(page,v) }
          }
    }
}