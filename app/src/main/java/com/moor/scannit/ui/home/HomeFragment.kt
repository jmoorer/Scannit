package com.moor.scannit.ui.home


import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.moor.scannit.R
import com.moor.scannit.data.Folder
import com.moor.scannit.databinding.FragmentHomeBinding
import com.moor.scannit.ui.SpacesItemDecoration
import java.util.*


class HomeFragment : Fragment(), FolderAdapter.FolderAdapterCallback {



    private lateinit var binding: FragmentHomeBinding
    private  val viewModel:HomeViewModel by viewModels()
    private  var folders= arrayListOf<Folder>()
    private  val folderAdapter:FolderAdapter= FolderAdapter(folders)


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
                adapter= folderAdapter.apply {
                    listener= this@HomeFragment
                }
                layoutManager= GridLayoutManager(context,3)
                addItemDecoration(SpacesItemDecoration(16))
            }
            scanButton.setOnClickListener {
                findNavController().navigate(R.id.cameraFragment)
            }
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getState().observe(viewLifecycleOwner, Observer { state->
            state?.let {
                folders.clear()
                if(state.folders.any()){
                    folders.addAll(state.folders)
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
                renameFolder( Folder(create_date = Date()))
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onLongClick(folder: Folder, view: View) {
        PopupMenu(requireContext(), view).apply {
            setOnMenuItemClickListener{
                when(it.itemId){
                    R.id.action_delete-> {
                        confirmDelete(folder)
                        true
                    }
                    R.id.action_rename->{
                        renameFolder(folder)
                        true
                    }
                    else-> false
                }
            }
            inflate(R.menu.menu_action_home)
            show()
        }
    }

    private fun confirmDelete(folder: Folder){
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Are you sure you want to delete ${folder.name}?")
        builder.setCancelable(false)
        builder.setPositiveButton("Yes"){ dialog, which ->
           viewModel.deleteFolder(folder)
        }
        builder.setNegativeButton("No",{_,a->})
        builder.show()
    }
    private fun renameFolder(folder: Folder){
        val editText= EditText(context);
        editText.setText(folder.name)
        val dialog = AlertDialog.Builder(context)
            .setMessage("Folder Name")
            .setView(editText)
            .setPositiveButton("OK") { _, i->
                val name=editText.text.toString()
                if(name.isNotEmpty()){
                    folder.name= name
                    viewModel.saveFolder(folder)
                }
            }
            .setNegativeButton("Cancel", null)
            .create();
        dialog.show();
    }


}
