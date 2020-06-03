package com.moor.scannit.ui.ocr

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

import com.google.common.util.concurrent.ListenableFuture
import com.moor.scannit.R
import com.moor.scannit.allPermissionsGranted
import com.moor.scannit.databinding.FragmentCameraBinding
import com.moor.scannit.databinding.FragmentOcrBinding
import com.moor.scannit.supportActionBar
import com.moor.scannit.toBitmap
import com.moor.scannit.ui.camera.CameraFragmentDirections
import com.moor.scannit.ui.camera.CameraViewModel
import java.io.File
import java.io.FileOutputStream
import java.security.Permission


class OcrFragment() : Fragment() {

    private lateinit var imageAnalyzer: ImageAnalysis
    private lateinit var imageCapture: ImageCapture
    private lateinit var imagePreview: Preview
    private lateinit var  cameraProviderFuture:ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraControl: CameraControl
    private lateinit var cameraInfo: CameraInfo

    private lateinit var binding: FragmentOcrBinding
    private  val previewView get()=binding.previewView
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    private val REQUEST_CODE_PERMISSIONS = 10


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentOcrBinding.inflate(layoutInflater,container,false).apply {
            if (allPermissionsGranted(REQUIRED_PERMISSIONS)) {
                previewView.post { startCamera() }
            } else {
                requestPermissions(
                    REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS
                )
            }
            cameraCaptureButton.setOnClickListener {
                takePicture()
            }
        }
        return binding.root
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun startCamera() {

        imageCapture = ImageCapture.Builder().apply {
            setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
        }.build()

        imagePreview = Preview.Builder().apply {
            setTargetAspectRatio(AspectRatio.RATIO_16_9)
            setTargetRotation(binding.previewView.display.rotation)
        }.build()

        imageAnalyzer = ImageAnalysis.Builder().apply {
            setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        }.build()


        val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            val camera = cameraProvider.bindToLifecycle(this, cameraSelector,imagePreview,imageCapture, imageAnalyzer)
            cameraControl = camera.cameraControl
            cameraInfo = camera.cameraInfo
            previewView.preferredImplementationMode = PreviewView.ImplementationMode.TEXTURE_VIEW
            imagePreview.setSurfaceProvider(previewView.createSurfaceProvider(cameraInfo))
            val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    val currentZoomRatio: Float = cameraInfo.zoomState.value?.zoomRatio ?: 0F
                    val delta = detector.scaleFactor
                    cameraControl.setZoomRatio(currentZoomRatio * delta)
                    return true
                }
            }

            val scaleGestureDetector = ScaleGestureDetector(context, listener)

            previewView.setOnTouchListener { _, event ->
                scaleGestureDetector.onTouchEvent(event)
                return@setOnTouchListener true
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }


    @SuppressLint("RestrictedApi")
    private fun takePicture() {
        binding.apply {
            shutter.visibility=View.VISIBLE
            val handler = Handler()
            handler.postDelayed({
                shutter.visibility= View.GONE
            }, 100)
        }

        imageCapture.takePicture(ContextCompat.getMainExecutor(requireContext()), object : ImageCapture.OnImageCapturedCallback() {
            override fun onError(exc: ImageCaptureException) {
                Log.e("", "Photo capture failed: ${exc.message}", exc)
            }

            @SuppressLint("UnsafeExperimentalUsageError")
            override fun onCaptureSuccess(imageProxy: ImageProxy) {
                val rotation = imageProxy.imageInfo.rotationDegrees
                val bitmap = imageProxy.image?.toBitmap(rotation)
                bitmap?.let {
                    val temp= File.createTempFile("images",".jpg",requireContext().cacheDir)
                    val output = FileOutputStream(temp)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output)
                    output.flush()
                    output.close()

                    val action= OcrFragmentDirections.actionOcrFragmentToOcrResultFragment(Uri.fromFile(temp))
                    findNavController().navigate(action)
                }
                super.onCaptureSuccess(imageProxy)
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted(REQUIRED_PERMISSIONS)) {
                previewView.post { startCamera() }
            } else {
                findNavController().popBackStack()
            }
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onDestroyView() {
        super.onDestroyView()
        CameraX.unbindAll()
    }

}
