package com.moor.scannit.ui.document

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer
import com.moor.scannit.BuildConfig
import com.moor.scannit.R
import com.moor.scannit.data.Page
import com.moor.scannit.databinding.FragmentPageBinding
import com.moor.scannit.generatePdf
import com.moor.scannit.supportActionBar

class PageFragment:Fragment() {

    private lateinit var pagerAdapter: PageViewAdapter
    private lateinit var binding: FragmentPageBinding
    val viewModel:DocumentViewModel by activityViewModels()

    val args:PageFragmentArgs by navArgs()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPageBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getDocument().observe(viewLifecycleOwner, Observer { doc->
            binding.apply {
                pagerAdapter = PageViewAdapter(doc.pages)

                pagesViewPager.adapter= pagerAdapter
                pagesViewPager.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        supportActionBar?.title="Page ${position+1} of ${doc.pages.count()}"
                    }
                })

                pagesViewPager.setCurrentItem(args.pageIndex,false)

                pageAddButton.setOnClickListener {
                    val action = PageFragmentDirections.actionPageFragmentToCameraFragment(doc.id)
                    findNavController().navigate(action)
                }
                deleteButton.setOnClickListener {

                    val page=  doc.pages[pagesViewPager.currentItem]
                    deletePage(page)
                }
                ocrButton.setOnClickListener {
                    val page=  doc.pages[pagesViewPager.currentItem]
                    val action= PageFragmentDirections.actionPageFragmentToOcrFragment(Uri.parse(page.uri))
                    findNavController().navigate(action)
                }


                downloadButton.setOnClickListener {
                    val page=  doc.pages[pagesViewPager.currentItem]
                    exportDocument(page)
                }
            }
        })

    }

    private  fun deletePage(page:Page){
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setMessage("Are you sure you want to delete this page?")
        builder.setCancelable(false)
        builder.setPositiveButton("Yes"){ dialog, which ->
            viewModel.removePage(page)
            pagerAdapter.notifyDataSetChanged()
            findNavController().popBackStack()
        }
        builder.setNegativeButton("No",null)
        builder.show()
    }
    private fun exportDocument(page:Page){
        val dialogView = layoutInflater.inflate(R.layout.dialog_export, null)
        val editText: EditText? =dialogView.findViewById(R.id.file_name_text_view)
        editText?.setText("Page.pdf")
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle("File Name")
            .setView(dialogView)
            .setPositiveButton("Save"){d,w->
                val file = requireContext().generatePdf(listOf(page),editText?.text.toString())
                val intent = Intent(Intent.ACTION_VIEW)
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                val uri: Uri? = FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID, file)
                intent.setDataAndType(uri, "application/pdf")
                startActivity(intent)
            }
        builder.create().show()
    }

}