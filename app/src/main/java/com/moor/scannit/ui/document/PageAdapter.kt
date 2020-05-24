package com.moor.scannit.ui.document

import android.net.Uri
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moor.scannit.data.Page
import com.moor.scannit.databinding.ItemPageBinding
import com.moor.scannit.inflater
import com.moor.scannit.load
import com.moor.scannit.ui.BoundViewHolder

class PageAdapter(val pages:List<Page>):RecyclerView.Adapter<BoundViewHolder<ItemPageBinding>>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BoundViewHolder<ItemPageBinding> {
        return  BoundViewHolder(ItemPageBinding.inflate(parent.inflater(),parent,false))
    }

    override fun getItemCount()= pages.size

    override fun onBindViewHolder(holder: BoundViewHolder<ItemPageBinding>, position: Int) {
          val page= pages[position]
          holder.binding.apply {
              previewImageView.load(Uri.parse(page.uri))
          }
    }
}