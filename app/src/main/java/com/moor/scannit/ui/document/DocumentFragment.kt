package com.moor.scannit.ui.document

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.EditText
import androidx.core.content.FileProvider
import androidx.core.view.marginStart
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.moor.scannit.*
import com.moor.scannit.data.Document
import com.moor.scannit.data.Page
import com.moor.scannit.databinding.DialogSaveBinding
import com.moor.scannit.databinding.FragmentDocumentBinding
import com.moor.scannit.ui.ProgressDialog
import com.moor.scannit.ui.RowHeightDecoration
import com.moor.scannit.ui.SpacesItemDecoration
import com.moor.scannit.ui.camera.CameraViewModel
import java.util.*


class DocumentFragment : Fragment(), PageAdapter.PageAdapterCallback {


    private lateinit var binding: FragmentDocumentBinding
    private  val args:DocumentFragmentArgs by navArgs()

    val viewModel:DocumentViewModel by activityViewModels()
    private val cameraViewModel: CameraViewModel by activityViewModels()

    val pages= arrayListOf<Page>()
    val pageAdapter=PageAdapter(pages).apply {
        listener= this@DocumentFragment
    }

    private lateinit var document:Document

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.loadDocument(args.documentId)
        viewModel.getDocument().observe(viewLifecycleOwner, Observer { doc->
            supportActionBar!!.title = doc.name
            document = doc
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
                adapter = pageAdapter
                layoutManager = GridLayoutManager(context,2)
                addItemDecoration(SpacesItemDecoration(16))
            }
            actionPick.setOnClickListener {
                pickImage()
            }
            actionCapture.setOnClickListener {
                val action = DocumentFragmentDirections.actionDocumentFragmentToCameraFragment(args.documentId)
                findNavController().navigate(action)
            }
           // addImageDial.inflate(R.menu.menu_action_home)
        }

        return  binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_document,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.action_rename->{
                renameDocument()
                return true
            }
            R.id.action_download->{
                exportDocument()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun renameDocument(){
        val dialogView = layoutInflater.inflate(R.layout.dialog_export, null)
        val editText:EditText? =dialogView.findViewById(R.id.file_name_text_view)
        editText?.setText(document.name)
        val dialog = MaterialAlertDialogBuilder(context)
            .setMessage("Document Name")
            .setView(dialogView)
            .setPositiveButton("OK") { _, i->
                val name=editText?.text.toString()
                if(name.isNotEmpty()){
                    document.name= name
                    viewModel.saveDocument(document)
                }
            }
            .setNegativeButton("Cancel", null)
            .create();
        dialog.show();
    }

    private fun exportDocument(){
        val dialogView = layoutInflater.inflate(R.layout.dialog_export, null)
        val editText:EditText? =dialogView.findViewById(R.id.file_name_text_view)
        editText?.setText("${document.name}.pdf")
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle("Export as")
            .setView(dialogView)
            .setPositiveButton("Save"){d,w->
                val file = requireContext().generatePdf(document.pages,editText?.text.toString())
                val intent = Intent(Intent.ACTION_VIEW)
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                val uri: Uri? = FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID, file)
                intent.setDataAndType(uri, "application/pdf")
                startActivity(intent)
            }
        builder.create().show()
    }

    override fun onLongClick(page: Page, view: View) {

    }

    override fun onClick(page: Page, view: View) {
       val action= DocumentFragmentDirections.actionDocumentFragmentToPageFragment(document.pages.indexOfId(page.id))
        findNavController().navigate(action)
    }


    private  fun pickImage(){
        val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(i, RESULT_LOAD_IMAGE)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            val selectedImage: Uri = data.data!!
            val path= requireContext().getRealPathFromUri(selectedImage)
            cameraViewModel.setDocument(document.id)
            cameraViewModel.setImage(BitmapFactory.decodeFile(path))
            findNavController().navigate(R.id.cropFragment)
        }
    }

}
