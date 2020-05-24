package com.moor.scannit.ui.home


import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.moor.scannit.FILENAME_FORMAT
import com.moor.scannit.R
import com.moor.scannit.data.Document
import com.moor.scannit.data.Folder
import com.moor.scannit.databinding.FragmentHomeBinding
import com.moor.scannit.generateFileName
import com.moor.scannit.ui.SpacesItemDecoration
import com.moor.scannit.ui.camera.CameraViewModel
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment(), DocumentAdapter.FolderAdapterCallback {



    private lateinit var binding: FragmentHomeBinding
    private  val viewModel:HomeViewModel by viewModels()
    private  val cameraViewModel: CameraViewModel by activityViewModels()
    private  var documents= arrayListOf<Document>()
    private  val documentAdapter:DocumentAdapter= DocumentAdapter(documents)


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
                layoutManager= GridLayoutManager(context,3)
                addItemDecoration(SpacesItemDecoration(16))
            }
            scanButton.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToCameraFragment(0)
                findNavController().navigate(action)
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
                    binding.folderListView.adapter?.notifyDataSetChanged()

                    binding.emptyView.visibility=View.INVISIBLE
                }else{
                    binding.emptyView.visibility=View.VISIBLE
                }


            }

        })

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home,menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.action_create->{
                renameFolder(Document().apply {
                    createDate = Date()
                    name = ""
                })
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onLongClick(document: Document, view: View) {
        PopupMenu(requireContext(), view).apply {
            setOnMenuItemClickListener{
                when(it.itemId){
                    R.id.action_delete-> {
                        confirmDelete(document)
                        true
                    }
                    R.id.action_rename->{
                        renameFolder(document)
                        true
                    }
                    else-> false
                }
            }
            inflate(R.menu.menu_action_home)
            show()
        }
    }

    override fun onClick(document: Document, view: View) {
        val action = HomeFragmentDirections.actionHomeFragmentToDocumentFragment(document.id)
        findNavController().navigate(action)
    }

    private fun confirmDelete(document: Document){
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Are you sure you want to delete ${document.name}?")
        builder.setCancelable(false)
        builder.setPositiveButton("Yes"){ dialog, which ->
           viewModel.deleteDocument(document)
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


}
