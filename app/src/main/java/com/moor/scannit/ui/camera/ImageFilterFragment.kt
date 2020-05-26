package com.moor.scannit.ui.camera

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.moor.scannit.R
import com.moor.scannit.databinding.FragmentImageFilterBinding
import com.uvstudio.him.photofilterlibrary.PhotoFilter
import jp.co.cyberagent.android.gpuimage.filter.*


class ImageFilterFragment : Fragment() {

    private var bitmap: Bitmap? = null
    private val viewModel:CameraViewModel by activityViewModels()
    private val binding: FragmentImageFilterBinding by lazy {
        FragmentImageFilterBinding.bind(requireView())
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_filter, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        val photoFilter = PhotoFilter(binding.surfaceView, object: OnProcessingCompletionListener {
//            override fun onProcessingComplete(bitmap: Bitmap) {
//                // Do anything with the bitmap save it or add another effect to it
//            }
//        })
        viewModel.getImage().observe(viewLifecycleOwner, Observer { bitmap->
          this.bitmap=bitmap
           // photoFilter.applyEffect(bitmap!!.copy(bitmap?.config,true),AutoFix())

        })

        var photoFilter = PhotoFilter()
        binding.filterListView.apply {
//            adapter= FilterAdapter().apply {
//                listener= object :FilterAdapter.FilterAdapterCallback{
//                    override fun onClick(filter: Filter) {
//                        photoFilter.applyEffect(bitmap!!.copy(bitmap?.config,true), filter)
//                    }
//                }
//            }
            adapter= FilterAdapter2().apply {
                listener={p->
                   val bmp= when(p){
                        0->bitmap
                        1-> photoFilter.one(requireContext(),bitmap)
                        2-> photoFilter.two(requireContext(),bitmap)
                        3-> photoFilter.three(requireContext(),bitmap)
                        4-> photoFilter.four(requireContext(),bitmap)
                        5-> photoFilter.five(requireContext(),bitmap)
                       6-> photoFilter.six(requireContext(),bitmap)
                       7-> photoFilter.seven(requireContext(),bitmap)
                       8-> photoFilter.eight(requireContext(),bitmap)
                       9-> photoFilter.nine(requireContext(),bitmap)
                       10-> photoFilter.ten(requireContext(),bitmap)
                       11-> photoFilter.eleven(requireContext(),bitmap)
                       12-> photoFilter.twelve(requireContext(),bitmap)
                       13-> photoFilter.thirteen(requireContext(),bitmap)
                       14-> photoFilter.fourteen(requireContext(),bitmap)
                       15-> photoFilter.fifteen(requireContext(),bitmap)
                       16 ->photoFilter.sixteen(requireContext(),bitmap)
                        else-> bitmap
                    }
                   binding.imageView.setImageBitmap(bmp)
                }
            }
            layoutManager= LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        }


    }


}
