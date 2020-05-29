package com.moor.scannit.ui.camera

import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moor.scannit.R
import com.moor.scannit.databinding.FragmentImageFilterBinding
import com.moor.scannit.supportActionBar
import com.moor.scannit.ui.SpacesItemDecoration
import com.zomato.photofilters.FilterPack
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.utils.ThumbnailItem
import com.zomato.photofilters.utils.ThumbnailsManager


class FilterFragment : Fragment() {


    private val viewModel:CameraViewModel by activityViewModels()
    private val binding: FragmentImageFilterBinding by lazy {
        FragmentImageFilterBinding.bind(requireView())
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_image_filter, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        supportActionBar?.title=""
        viewModel.getImage().observe(viewLifecycleOwner, Observer { bitmap->
          binding.apply {

              bitmap?.let {


                  imageView.setImageBitmap(bitmap)

                  val filters: List<Filter> =
                      FilterPack.getFilterPack(activity)

                  for (filter in filters) {
                      val item = ThumbnailItem()
                      item.image =bitmap.copy(bitmap.config,true)
                      item.filter = filter
                      item.filterName = filter.name
                      ThumbnailsManager.addThumb(item)
                  }
                  filtersListView.apply {
                    adapter= FilterAdapter(ThumbnailsManager.processThumbs(requireContext())).apply {
                        listener= object :FilterAdapter.FilterAdapterCallback{
                            override fun onClick(item:ThumbnailItem) {
                                imageView.setImageBitmap(item.filter.processFilter(bitmap.copy(bitmap.config,true)))
                            }
                        }
                    }
                  layoutManager= LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
                  addItemDecoration(SpacesItemDecoration(8))
                }
              }
          }
        })


    }

    override fun onDestroyView() {
        super.onDestroyView()
        ThumbnailsManager.clearThumbs()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_filter, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.action_save->{
                val bitmap = binding.imageView.drawToBitmap()
                val documentId  =  viewModel.saveCroppedImage(bitmap)
                val action= FilterFragmentDirections.actionImageFilterFragmentToDocumentFragment(documentId)
                findNavController().navigate(action)
            }
        }
        return super.onOptionsItemSelected(item)

    }
}
