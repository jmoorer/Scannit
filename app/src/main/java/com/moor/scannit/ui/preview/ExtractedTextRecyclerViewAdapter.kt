package com.moor.scannit.ui.preview

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.moor.scannit.R




import kotlinx.android.synthetic.main.fragment_extracted_text.view.*


class ExtractedTextRecyclerViewAdapter(val values: MutableList<FirebaseVisionText.TextBlock>) : RecyclerView.Adapter<ExtractedTextRecyclerViewAdapter.ViewHolder>() {


    init {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_extracted_text, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.textBlock.text=item.text

    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val textBlock: TextView = view.textBlock


    }
}
