package com.moor.scannit.ui.ocr

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.moor.scannit.R
import com.moor.scannit.databinding.FragmentImageBinding


class ImageFragment : Fragment() {


    private val viewModel:OcrViewModel by activityViewModels()

    private val binding: FragmentImageBinding by lazy {
        FragmentImageBinding.bind(requireView())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_image, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getState().observe(viewLifecycleOwner, Observer { state->

            state.bitmap?.let{ bitmap->
                val tempBitmap = Bitmap.createBitmap(
                    bitmap.width,
                    bitmap.height,
                    Bitmap.Config.RGB_565
                )
                val tempCanvas = Canvas(tempBitmap)
                val paint= Paint().apply {
                    isAntiAlias = true
                    color =resources.getColor(R.color.colorAccent)
                    style = Paint.Style.STROKE
                    strokeWidth=3f
                }
                tempCanvas.drawBitmap(bitmap,0f,0f,null)
                state.boxes.forEach {
                    tempCanvas.drawRect(it,paint)
                }

                binding.imageView.setImageBitmap(tempBitmap)

            }

        })
    }

}
