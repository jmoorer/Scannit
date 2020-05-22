package com.moor.scannit.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.moor.scannit.toBitmap

class FreezeAnalyzer(private val callback: FreezeCallback) : ImageAnalysis.Analyzer {
    interface FreezeCallback {
        fun onLastFrameCaptured(bitmap: Bitmap)
    }

    private var flag = false



    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        if(flag){
            flag = false
            imageProxy.image?.let {
                val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                val bitmap = it.toBitmap(rotationDegrees)
                // ...
                callback.onLastFrameCaptured(bitmap)
            }
        }
        imageProxy.close()



    }

    fun freeze(){
        flag = true
    }


}