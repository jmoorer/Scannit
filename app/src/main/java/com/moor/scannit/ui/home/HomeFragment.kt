package com.moor.scannit.ui.home


import android.Manifest
import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.moor.scannit.R
import com.moor.scannit.RESULT_LOAD_IMAGE
import com.moor.scannit.data.Document
import com.moor.scannit.databinding.FragmentHomeBinding
import com.moor.scannit.getRealPathFromUri
import com.moor.scannit.ui.AdapterCallback
import com.moor.scannit.ui.camera.CameraViewModel
import java.util.*


class HomeFragment : Fragment(), AdapterCallback<Document> {

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

                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(DividerItemDecoration(context,DividerItemDecoration.VERTICAL))
                helper.attachToRecyclerView(this)
            }
            scanButton.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToCameraFragment(0)
                findNavController().navigate(action)
            }

            pickButton.setOnClickListener {
                if(allPermissionsGranted()){
                    pickImage()
                }else{
                    requestPermissions(
                        REQUIRED_PERMISSIONS,
                        REQUEST_CODE_PERMISSIONS
                    )
                }
            }
            ocrButton.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToOcrFragment()
                findNavController().navigate(action)
            }
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getDocuments().observe(viewLifecycleOwner, Observer {docs->
            documents.clear()
            if(docs.any()){
                documents.addAll(docs)
                binding.emptyView.visibility=View.INVISIBLE
            }else{
                binding.emptyView.visibility=View.VISIBLE
            }
            documentAdapter.notifyDataSetChanged()
        })

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home,menu)
        val searchManager = context?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.search).actionView as SearchView).apply {
            maxWidth = Integer.MAX_VALUE;
            setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
            setOnQueryTextListener(object :SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(text: String): Boolean {
                    viewModel.filterDocuments(text)
                    return true
                }
            })

        }
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
                               if (ids.any()){
                                   val builder = MaterialAlertDialogBuilder(requireContext())
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
                               }else{
                                   callback.finishActionMode()
                               }

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

    private  fun pickImage(){
        val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(i, RESULT_LOAD_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            val selectedImage: Uri = data.data!!
            val path= requireContext().getRealPathFromUri(selectedImage)
            cameraViewModel.setDocument(0)
            cameraViewModel.setImage(BitmapFactory.decodeFile(path))
            findNavController().navigate(R.id.cropFragment)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_sort_name_asc->viewModel.sortDocuments(HomeViewModel.SortOrder.ASCENDING_NAME)
            R.id.action_sort_name_desc->viewModel.sortDocuments(HomeViewModel.SortOrder.DESCENDING_NAME)
            R.id.action_sort_date_asc->viewModel.sortDocuments(HomeViewModel.SortOrder.ASCENDING_DATE)
            R.id.action_sort_date_desc->viewModel.sortDocuments(HomeViewModel.SortOrder.DESCENDING_DATE)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private val helper=ItemTouchHelper(object :ItemTouchHelper.Callback(){
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN,0)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            dragged: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val targetPosition= target.adapterPosition
            val draggedPosition= dragged.adapterPosition

            Collections.swap(documents,draggedPosition,targetPosition)
            documentAdapter.notifyItemMoved(draggedPosition,targetPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

        }
    })

}
