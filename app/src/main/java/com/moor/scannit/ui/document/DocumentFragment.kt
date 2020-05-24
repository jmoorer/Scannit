package com.moor.scannit.ui.document

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.moor.scannit.data.Page
import com.moor.scannit.databinding.FragmentDocumentBinding


class DocumentFragment : Fragment() {


    private lateinit var binding: FragmentDocumentBinding
    private  val args:DocumentFragmentArgs by navArgs()

    val viewModel:DocumentViewModel by viewModels()

    val pages= arrayListOf<Page>()
    val pageAdapter=PageAdapter(pages)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.loadDocument(args.documentId).observe(viewLifecycleOwner, Observer { doc->
            (activity as AppCompatActivity?)!!.supportActionBar!!.title = doc.name
           pages.clear()
           doc.pages.any().let {
               pages.addAll(doc.pages)
               pageAdapter.notifyDataSetChanged()
           }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDocumentBinding.inflate( inflater,container, false).apply {

            pagesGridView.apply {
                adapter= pageAdapter
                layoutManager= GridLayoutManager(context,2)
            }
            addPageButtonButton.setOnClickListener {
                val action= DocumentFragmentDirections.actionDocumentFragmentToCameraFragment(args.documentId)
                findNavController().navigate(action)
            }
        }

        return  binding.root
    }


}
