package com.moor.scannit.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

open class BoundViewHolder<T: ViewBinding>(val binding: T):RecyclerView.ViewHolder(binding.root) {

}
//
//abstract class BoundAdapter<T:ViewBinding>:RecyclerView.Adapter<BoundViewHolder<T>>(){
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoundViewHolder<T> {
//        var inflater= LayoutInflater.from(parent.context)
//        val binding = inflater.inflate()
//            . .invoke(null, inflater, container, false) as ViewBinding
//       return  BoundViewHolder()
//    }
//
//
//
//    override fun onBindViewHolder(holder: BoundViewHolder<T>, position: Int) {
//        TODO("Not yet implemented")
//    }
//
//}