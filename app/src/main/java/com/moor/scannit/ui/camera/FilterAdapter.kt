package com.moor.scannit.ui.camera

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moor.scannit.databinding.ItemFilterBinding
import com.moor.scannit.inflater
import com.moor.scannit.ui.BoundViewHolder
import com.mukesh.imageproccessing.filters.*

class FilterAdapter:RecyclerView.Adapter<BoundViewHolder<ItemFilterBinding>>() {

    interface  FilterAdapterCallback{
        fun onClick(filter: Filter)
    }

    var listener:FilterAdapterCallback?=null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BoundViewHolder<ItemFilterBinding> {
       return  BoundViewHolder(ItemFilterBinding.inflate(parent.inflater(),parent,false))
    }

    override fun getItemCount(): Int {
      return  filterMap.size
    }

    override fun onBindViewHolder(holder: BoundViewHolder<ItemFilterBinding>, position: Int) {

        var entry= filterMap.entries.elementAt(position)
        holder.binding.filterTextView.text= entry.key
        holder.binding.root.setOnClickListener {
            listener?.onClick(entry.value)
        }
    }

    val filterMap= mapOf(
        "None" to None(),
        "Auto-fix" to AutoFix(),
        "Brightness" to Brightness(),
        "Contrast"  to Contrast(),
        "Cross Process" to CrossProcess(),
        "Documentary" to Documentary(),
        "Duo Tone" to DuoTone(),
        "Fill-Light" to FillLight(),
        "FishEye" to FishEye(),
        "Flip Horizontally" to FlipHorizontally(),
        "Flip Vertically" to FlipVertically(),
        "Grain" to Grain(),
        "Grayscale" to Grayscale(),
        "Highlight" to Highlight(),
        "Lomoish" to Lomoish(),
        "Negative" to Negative(),
        "Posterize" to Posterize(),
        "Rotate" to Rotate(),
        "Saturate" to Saturate(),
        "Sepia" to Sepia(),
        "Sharpen" to Sharpen(),
        "Temperature" to Temperature(),
        "Tint" to Tint(),
        "Vignette" to Vignette()
    )
}