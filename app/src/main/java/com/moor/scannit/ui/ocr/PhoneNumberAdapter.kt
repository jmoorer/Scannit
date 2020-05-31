package com.moor.scannit.ui.ocr

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moor.scannit.databinding.ItemPhoneNumberBinding
import com.moor.scannit.inflater
import com.moor.scannit.ui.AdapterCallback
import com.moor.scannit.ui.BoundViewHolder

class PhoneNumberAdapter(val numbers: List<String>):RecyclerView.Adapter<BoundViewHolder<ItemPhoneNumberBinding>>() {

    var listener:AdapterCallback<String>?=null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BoundViewHolder<ItemPhoneNumberBinding> {
        return BoundViewHolder(ItemPhoneNumberBinding.inflate(parent.inflater(),parent,false))
    }

    override fun getItemCount(): Int {
       return  numbers.size
    }

    override fun onBindViewHolder(holder: BoundViewHolder<ItemPhoneNumberBinding>, position: Int) {
        holder.binding.apply {
            val text=  numbers[position]
            numberTextView.text=text
            callButton.setOnClickListener {
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