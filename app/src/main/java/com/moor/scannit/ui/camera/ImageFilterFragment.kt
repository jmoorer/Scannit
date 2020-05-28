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
import ja.burhanrashid52.photoeditor.PhotoEditor


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
            binding.photoEditorView.source.setImageBitmap(bitmap)
        })

        var photoFilter = PhotoFilter()

        val mPhotoEditor = PhotoEditor.Builder(requireContext(), binding.photoEditorView)
            .setPinchTextScalable(true)
            .build()
        binding.filterListView.apply {
//            adapter= FilterAdapter().apply {
//                listener= object :FilterAdapter.FilterAdapterCallback{
//                    override fun onClick(filter: Filter) {
//                        photoFilter.applyEffect(bitmap!!.copy(bitmap?.config,true), filter)
//                    }
//                }
//            }
            var filters=ja.burhanrashid52.photoeditor.PhotoFilter.values()
            adapter= FilterAdapter2().apply {
                listener={p->
                    mPhotoEditor.setFilterEffect(filters[p])
//                  when(p){
//                        0->mPhotoEditor.setFilterEffect(ja.burhanrashid52.photoeditor.PhotoFilter.BLACK_WHITE)
//                        1-> mPhotoEditor.setFilterEffect(ja.burhanrashid52.photoeditor.PhotoFilter.BRIGHTNESS)
//                        2-> mPhotoEditor.setFilterEffect(ja.burhanrashid52.photoeditor.PhotoFilter.CROSS_PROCESS)
//                        3->mPhotoEditor.setFilterEffect(ja.burhanrashid52.photoeditor.PhotoFilter.DUE_TONE)
//                        4-> mPhotoEditor.setFilterEffect(ja.burhanrashid52.photoeditor.PhotoFilter.NEGATIVE)
//                        5-> mPhotoEditor.setFilterEffect(ja.burhanrashid52.photoeditor.PhotoFilter.LOMISH)
//                       6->mPhotoEditor.setFilterEffect(ja.burhanrashid52.photoeditor.PhotoFilter.FILL_LIGHT)
//                       7-> mPhotoEditor.setFilterEffect(ja.burhanrashid52.photoeditor.PhotoFilter.GRAY_SCALE)
//                       8-> photoFilter.eight(requireContext(),bitmap)
//                       9-> photoFilter.nine(requireContext(),bitmap)
//                       10-> photoFilter.ten(requireContext(),bitmap)
//                       11-> photoFilter.eleven(requireContext(),bitmap)
//                       12-> photoFilter.twelve(requireContext(),bitmap)
//                       13-> photoFilter.thirteen(requireContext(),bitmap)
//                       14-> photoFilter.fourteen(requireContext(),bitmap)
//                       15-> photoFilter.fifteen(requireContext(),bitmap)
//                       16 ->photoFilter.sixteen(requireContext(),bitmap)
//                        else-> bitmap
//                    }
                  // binding.imageView.setImageBitmap(bmp)
                }
            }
            layoutManager= LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)




        }


    }


}
