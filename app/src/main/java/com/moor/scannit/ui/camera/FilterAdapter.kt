package com.moor.scannit.ui.camera

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.moor.scannit.databinding.ItemFilterBinding
import com.moor.scannit.inflater
import com.moor.scannit.ui.BoundViewHolder
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.utils.ThumbnailItem

class FilterAdapter(val items :List<ThumbnailItem>):RecyclerView.Adapter<BoundViewHolder<ItemFilterBinding>>() {

    interface  FilterAdapterCallback{
        fun onClick(item:ThumbnailItem)
    }

    private var selectedIndex = RecyclerView.NO_POSITION

    var listener:FilterAdapterCallback?=null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BoundViewHolder<ItemFilterBinding> {
       return  BoundViewHolder(ItemFilterBinding.inflate(parent.inflater(),parent,false))
    }

    override fun getItemCount(): Int {
      return  items.size
    }

    override fun onBindViewHolder(holder: BoundViewHolder<ItemFilterBinding>, position: Int) {

        val entry= items[position]
        holder.binding.apply {
            filterTextView.text= entry.filterName
            filterImageView.setImageBitmap(entry.image)
            selectedIcon.isVisible = position==selectedIndex
            root.setOnClickListener {
                notifyItemChanged(selectedIndex)
                selectedIndex = position
                listener?.onClick(entry)
                notifyItemChanged(position)

            }
        }

    }


}