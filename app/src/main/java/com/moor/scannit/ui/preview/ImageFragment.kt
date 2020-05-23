package com.moor.scannit.ui.preview

import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetector
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetectorOptions
import com.moor.scannit.databinding.FragmentImageBinding
import com.moor.scannit.loadBitmap
import java.io.IOException


class ImageFragment : Fragment() {


    private lateinit var objectDetector: FirebaseVisionObjectDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding=FragmentImageBinding.inflate(inflater,container,false)
        arguments?.getString("image")?.let {
            val uri= Uri.parse(it)
            binding.imageView.setImageURI(uri)
            val options = FirebaseVisionObjectDetectorOptions.Builder()
                .setDetectorMode(FirebaseVisionObjectDetectorOptions.SINGLE_IMAGE_MODE)
                //.enableMultipleObjects()
                .enableClassification()  // Optional
                .build()
            objectDetector = FirebaseVision.getInstance().getOnDeviceObjectDetector(options)


            val image: FirebaseVisionImage
            try {

                val bmp = loadBitmap(uri)
                val cnvs = Canvas(bmp)
                val paint = Paint()
                image = FirebaseVisionImage.fromBitmap(bmp)
                paint.color = Color.RED
                paint.style = Paint.Style.STROKE
                paint.strokeWidth=5f
                objectDetector.processImage(image).
                addOnSuccessListener {objects->
                   objects.forEach{o->
                       cnvs.drawRect(o.boundingBox,paint)
                   }
                   binding.imageView.setImageBitmap(bmp)
                }.
                addOnFailureListener{ex->

                }
            } catch (e: IOException) {
                e.printStackTrace()
            }




        }
        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance(uri: String) =
            ImageFragment().apply {
                arguments = Bundle().apply {
                    putString("image", uri)
                }
            }
    }
}
