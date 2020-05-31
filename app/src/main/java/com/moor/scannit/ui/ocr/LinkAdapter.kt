package com.moor.scannit.ui.ocr

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moor.scannit.databinding.ItemLinkBinding
import com.moor.scannit.inflater
import com.moor.scannit.ui.AdapterCallback
import com.moor.scannit.ui.BoundViewHolder

class LinkAdapter(private val links: List<String>):RecyclerView.Adapter<BoundViewHolder<ItemLinkBinding>>() {

    var listener:AdapterCallback<String>?=null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BoundViewHolder<ItemLinkBinding> {
        return BoundViewHolder(ItemLinkBinding.inflate(parent.inflater(),parent,false))
    }

    override fun getItemCount(): Int {
       return  links.size
    }

    override fun onBindViewHolder(holder: BoundViewHolder<ItemLinkBinding>, position: Int) {
        holder.binding.apply {
            val text=  links[position]
            linkTextView.text=text
            linkButton.setOnClickListener {
                listener?.onClick(text,it)
            }
            shareButton.setOnClickListener {
                listener?.onClick(text,it)
            }

            root.setOnLongClickListener {
                listener?.onLongClick(text,it)
                return@setOnLongClickListener true
            }
        }
    }
}