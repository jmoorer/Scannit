package com.moor.scannit.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.objects.FirebaseVisionObject
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetector
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetectorOptions
import java.util.concurrent.atomic.AtomicBoolean

class DetectorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
)  : View(context, attrs, defStyleAttr){

    private val pointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLUE
        style = Paint.Style.STROKE
    }
    var boxes = listOf<Rect>()
        set(value) {
            field = value
            invalidate()
        }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            for (box  in boxes) {
                drawRect(box,pointPaint)
            }
        }
    }
}


class ObjectAnalyzer : ImageAnalysis.Analyzer {

    private var isAnalyzing = AtomicBoolean(false)
    var objectListListener: ((List<Rect>,rotationDegrees: Int) -> Unit)? = null
    private  val dectector: FirebaseVisionObjectDetector by lazy {
        val options = FirebaseVisionObjectDetectorOptions.Builder()
            .setDetectorMode(FirebaseVisionObjectDetectorOptions.SINGLE_IMAGE_MODE)
            .enableMultipleObjects()
            .enableClassification()  // Optional
            .build()
        FirebaseVision.getInstance().getOnDeviceObjectDetector(options)
    }


    private fun getRotationConstant(rotationDegrees: Int): Int {
        return when (rotationDegrees) {
            90 -> FirebaseVisionImageMetadata.ROTATION_90
            180 -> FirebaseVisionImageMetadata.ROTATION_180
            270 -> FirebaseVisionImageMetadata.ROTATION_270
            else -> FirebaseVisionImageMetadata.ROTATION_0
        }
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(image: ImageProxy) {
        if (image.image!=null && isAnalyzing.compareAndSet(false,true)) {
            val cameraImage = image.image!!
            val rotationDegrees = image.imageInfo.rotationDegrees
            val firebaseVisionImage = FirebaseVisionImage.fromMediaImage(cameraImage,getRotationConstant(rotationDegrees))
            dectector.processImage(firebaseVisionImage)
                .addOnSuccessListener{ objects->
                    isAnalyzing.set(false)
                    val bounds = mutableListOf<Rect>()
                    for (o in objects){
                        bounds+= o.boundingBox
                    }
                    objectListListener?.invoke(bounds,rotationDegrees)
                }
                .addOnFailureListener{
                    isAnalyzing.set(false)
                }
        }
        image.close()
    }
}