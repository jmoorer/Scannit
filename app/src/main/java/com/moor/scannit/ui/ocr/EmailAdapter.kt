package com.moor.scannit.ui.ocr

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moor.scannit.databinding.ItemEmailBinding
import com.moor.scannit.inflater
import com.moor.scannit.ui.AdapterCallback
import com.moor.scannit.ui.BoundViewHolder

class EmailAdapter(private val emails: List<String>):RecyclerView.Adapter<BoundViewHolder<ItemEmailBinding>>() {

    var listener:AdapterCallback<String>?=null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BoundViewHolder<ItemEmailBinding> {
        return BoundViewHolder(ItemEmailBinding.inflate(parent.inflater(),parent,false))
    }

    override fun getItemCount(): Int {
       return  emails.size
    }

    override fun onBindViewHolder(holder: BoundViewHolder<ItemEmailBinding>, position: Int) {
        holder.binding.apply {
            val text=  emails[position]
            emailTextView.text=text
            emailButton.setOnClickListener {
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