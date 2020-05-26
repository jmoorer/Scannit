package com.moor.scannit.ui.camera

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moor.scannit.databinding.ItemFilterBinding
import com.moor.scannit.inflater
import com.moor.scannit.ui.BoundViewHolder
import com.mukesh.imageproccessing.filters.*

class FilterAdapter2:RecyclerView.Adapter<BoundViewHolder<ItemFilterBinding>>() {


    var listener:((Int)-> Unit)?=null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BoundViewHolder<ItemFilterBinding> {
       return  BoundViewHolder(ItemFilterBinding.inflate(parent.inflater(),parent,false))
    }

    override fun getItemCount(): Int {
      return  16
    }

    override fun onBindViewHolder(holder: BoundViewHolder<ItemFilterBinding>, position: Int) {


        holder.binding.filterTextView.text= "$position"
        holder.binding.root.setOnClickListener {
            listener?.invoke(position)
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