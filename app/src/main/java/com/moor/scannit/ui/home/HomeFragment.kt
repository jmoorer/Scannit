package com.moor.scannit.ui.home


import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.moor.scannit.R
import com.moor.scannit.data.Document
import com.moor.scannit.databinding.FragmentHomeBinding
import com.moor.scannit.getRealPathFromUri
import com.moor.scannit.ui.AdapterCallback
import com.moor.scannit.ui.camera.CameraViewModel


class HomeFragment : Fragment(), AdapterCallback<Document> {


    private val RESULT_LOAD_IMAGE: Int=101
    private lateinit var binding: FragmentHomeBinding
    private  val viewModel:HomeViewModel by viewModels()
    private val cameraViewModel: CameraViewModel by activityViewModels()
    private  var documents= arrayListOf<Document>()
    private  val documentAdapter:DocumentAdapter= DocumentAdapter(documents)
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val REQUEST_CODE_PERMISSIONS = 10



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater,container,false).apply {
            folderListView.apply {
                adapter= documentAdapter.apply {
                    listener= this@HomeFragment
                }
                //layoutManager= GridLayoutManager(context,3)
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(DividerItemDecoration(context,DividerItemDecoration.VERTICAL))
            }
            scanButton.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToCameraFragment(0)
                findNavController().navigate(action)
            }
            bar.setOnMenuItemClickListener { item ->
                when(item.itemId){
                    R.id.action_open->{
                        cameraViewModel.setDocument(0)
                        if(allPermissionsGranted()){
                            pickImage()
                        }else{
                            requestPermissions(
                                REQUIRED_PERMISSIONS,
                                REQUEST_CODE_PERMISSIONS
                            )
                        }
                    }
                }
                true
            }
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getState().observe(viewLifecycleOwner, Observer { state->
            state?.let {
                documents.clear()
                if(state.documents.any()){
                    documents.addAll(state.documents)
                    binding.emptyView.visibility=View.INVISIBLE
                }else{
                    binding.emptyView.visibility=View.VISIBLE
                }
                documentAdapter.notifyDataSetChanged()
            }

        })

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home,menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
               pickImage()
            }
        }
    }

    override fun onLongClick(document: Document, view: View) {


          PrimaryActionModeCallback().also {callback->
               callback.onActionItemClickListener= object : PrimaryActionModeCallback.OnActionItemClickListener {
                   override fun onActionItemClick(item: MenuItem) {
                       when(item.itemId){
                           R.id.action_delete->{
                               val ids= documentAdapter.selectedIds
                               val builder = AlertDialog.Builder(requireContext())
                               builder.setMessage("Are you sure you want to delete ${ids.size} document(s) ?")
                               builder.setCancelable(false)
                               builder.setPositiveButton("Yes"){ dialog, which ->
                                   viewModel.deleteDocuments(ids.toLongArray())
                               }
                               builder.setNegativeButton("No",{_,a->})
                               builder.setOnDismissListener {
                                   callback.finishActionMode()
                               }
                               builder.show()

                           }
                       }
                   }

                   override fun onDestroyActionMode() {
                      documentAdapter.canEdit=false
                   }
               }
              callback.startActionMode(view,R.menu.menu_action_home)
          }





         documentAdapter.canEdit=true
         documentAdapter.selectedIds.apply {
             clear()
             add(document.id)
         }

    }

    override fun onClick(document: Document, view: View) {
        val action = HomeFragmentDirections.actionHomeFragmentToDocumentFragment(document.id)
        findNavController().navigate(action)
    }

    private fun confirmDelete(ids:Set<Long>){
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Are you sure you want to delete ${ids.size} document(s) ?")
        builder.setCancelable(false)
        builder.setPositiveButton("Yes"){ dialog, which ->
           viewModel.deleteDocuments(ids.toLongArray())
        }
        builder.setNegativeButton("No",{_,a->})
        builder.show()
    }
    private fun renameFolder(document: Document){
        val editText= EditText(context);
        editText.setText(document.name)
        val dialog = AlertDialog.Builder(context)
            .setMessage("Folder Name")
            .setView(editText)
            .setPositiveButton("OK") { _, i->
                val name=editText.text.toString()
                if(name.isNotEmpty()){
                    document.name= name
                    viewModel.saveDocument(document)
                }
            }
            .setNegativeButton("Cancel", null)
            .create();
        dialog.show();
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            val selectedImage: Uri = data.data!!
            val path= getRealPathFromUri(selectedImage)

            cameraViewModel.setImage(BitmapFactory.decodeFile(path))
            findNavController().navigate(R.id.imageProccessFragment)
        }
    }

    private  fun pickImage(){
        val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(i, RESULT_LOAD_IMAGE)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

}
