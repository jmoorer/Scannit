package com.moor.scannit.ui.camera

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.moor.scannit.R
import com.moor.scannit.databinding.FragmentImageProcessBinding
import com.zomato.photofilters.FilterPack
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.utils.ThumbnailItem
import com.zomato.photofilters.utils.ThumbnailsManager


class ImageProcessFragment : Fragment(){

    private lateinit var binding: FragmentImageProcessBinding
    private val viewModel:CameraViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getImage().observe(viewLifecycleOwner, Observer {bitmap->
            binding.apply {
                imageView.setImageBitmap(bitmap)


                val filters: List<Filter> =
                    FilterPack.getFilterPack(activity)

                for (filter in filters) {
                    val item = ThumbnailItem()
                    item.image =bitmap?.copy(bitmap.config,true)
                    item.filter = filter
                    item.filterName = filter.name
                    ThumbnailsManager.addThumb(item)
                }

                filtersListView.apply {
                    adapter= FilterAdapter(ThumbnailsManager.processThumbs(requireContext())).apply {
                        listener=object: FilterAdapter.FilterAdapterCallback {
                            override fun onClick(item: ThumbnailItem) {
                               val re= imageView.cropRect
                               imageView.setImageBitmap(item.filter.processFilter(bitmap?.copy(bitmap.config,true)))
                               imageView.cropRect=re
                            }
                        }
                    }
                    layoutManager= LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)

                }
                doneButton.setOnClickListener {
                     bitmap?.let{
//                        val documentId  =  viewModel.saveCroppedImage(bitmap)
//                        val action= ImageProcessFragmentDirections.actionImageProccessFragmentToDocumentFragment(documentId)
//                        findNavController().navigate(action)
                     }
                }

                ocrButton.setOnClickListener {

                    findNavController().navigate(R.id.imageFilterFragment)
//                    val temp=File.createTempFile("images",".jpg",requireContext().cacheDir)
//                    val output = FileOutputStream(temp)
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output)
//                    output.flush()
//                    output.close()
//                    findNavController().navigate(R.id.extractedTextFragment, bundleOf("image_uri" to Uri.fromFile(temp).toString() ))
                }

            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentImageProcessBinding.inflate(inflater,container,false).apply {
            cropButton.setOnClickListener {
                findNavController().navigate(R.id.cropFragment)
            }
        }
        return binding.root
    }

}
