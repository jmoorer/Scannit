package com.moor.scannit.ui.camera

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
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
                cropImageView.setImageBitmap(bitmap)

                val filters: List<Filter> =
                    FilterPack.getFilterPack(activity)

                for (filter in filters) {
                    val item = ThumbnailItem()
                    item.image =bitmap?.copy(bitmap.config,true)
                    item.filter = filter
                    item.filterName = filter.name
                    ThumbnailsManager.addThumb(item)
                }

                filterListView.apply {
                    adapter= FilterAdapter(ThumbnailsManager.processThumbs(requireContext())).apply {
                        listener=object: FilterAdapter.FilterAdapterCallback {
                            override fun onClick(item: ThumbnailItem) {

                               imageView.setImageBitmap(item.filter.processFilter(bitmap?.copy(bitmap.config,true)))

                            }
                        }
                    }
                    layoutManager= LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)

                }
                var animFadein = AnimationUtils.loadAnimation(
                    activity?.applicationContext,
                    R.anim.fade_in)
                discardButton.setOnClickListener {
                    flipper.startAnimation(animFadein)
                    flipper.showNext()
                }


            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentImageProcessBinding.inflate(inflater,container,false).apply {
//            cropButton.setOnClickListener {
//                findNavController().navigate(R.id.cropFragment)
//            }
        }
        return binding.root
    }

}
