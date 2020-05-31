package com.moor.scannit.ui.ocr

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.moor.scannit.R
import com.moor.scannit.databinding.FragmentListBinding
import com.moor.scannit.ui.AdapterCallback


class LinkFragment : Fragment(), AdapterCallback<String> {

    private val viewModel:OcrViewModel by activityViewModels()


    private val binding: FragmentListBinding by lazy {
        FragmentListBinding.bind(requireView())
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getState().observe(viewLifecycleOwner, Observer { state->
            binding.apply {
                itemListView.apply {
                    adapter= LinkAdapter(state.links).apply {
                        listener= this@LinkFragment
                    }
                    layoutManager= LinearLayoutManager(requireContext())
                }
            }
        })
    }

    override fun onLongClick(item: String, view: View) {

    }

    override fun onClick(item: String, view: View) {
        when(view.id){
            R.id.share_button->{
                val sharingIntent = Intent(Intent.ACTION_SEND)
                sharingIntent.type = "text/plain"
                sharingIntent.putExtra(Intent.EXTRA_TEXT, item)
                startActivity(Intent.createChooser(sharingIntent, "Share via"))
            }
            R.id.link_button->{
                var url= item
                if (!url.startsWith("https://") && !url.startsWith("http://")){
                    url = "http://$url";
                }
                val intent = Intent(Intent.ACTION_VIEW,Uri.parse(url))
                startActivity(Intent.createChooser(intent, "Open link using ..."));
            }
        }
    }

}
