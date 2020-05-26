package com.moor.scannit.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Size
import android.view.TextureView
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import com.moor.scannit.R
import com.moor.scannit.databinding.ActivityScanBinding
import com.moor.scannit.toBitmap
import java.io.File
import java.nio.file.Files.createFile
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

private const val REQUEST_CODE_PERMISSIONS = 10

private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

class ScanActivity : AppCompatActivity() {

    private lateinit var imageAnalysis: ImageAnalysis
    private lateinit var imagePreview: Preview
    private lateinit var previewView: PreviewView
    private lateinit var binding: ActivityScanBinding
    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>


    private lateinit var imageCapture: ImageCapture

    private val executor = Executors.newSingleThreadExecutor()

    private lateinit var outputDirectory: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        outputDirectory = getOutputDirectory(this)
        binding.cameraCaptureButton.setOnClickListener {
            takePicture()
        }
        previewView= binding.previewView
        if (allPermissionsGranted()) {
            previewView.post { startCamera() }
        } else {
            requestPermissions(
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }
    private fun startCamera() {
        val resolutionSize = Size(previewView.width, previewView.height)

        imageCapture = ImageCapture.Builder().apply {
            setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
        }.build()

//        imageAnalysis = ImageAnalysis.Builder().apply {
//            setTargetResolution(resolutionSize)
//            setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//        }.build()
//
//        analyzer = FreezeAnalyzer(object : FreezeAnalyzer.FreezeCallback {
//            override fun onLastFrameCaptured(bitmap: Bitmap) {
//                runOnUiThread {
//                    binding.apply {
//                        previewView.visibility = View.INVISIBLE
//                       capture.visibility= View.VISIBLE
//                        capture.setImageBitmap(bitmap)
//                    }
//
//                }
//            }
//        })
//        imageAnalysis.setAnalyzer(executor, analyzer)

        imagePreview = Preview.Builder().apply {
            setTargetAspectRatio(AspectRatio.RATIO_16_9)
            setTargetRotation(previewView.display.rotation)
        }.build()
        val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            val camera = cameraProvider.bindToLifecycle(this, cameraSelector,imagePreview,imageCapture)
            previewView.preferredImplementationMode = PreviewView.ImplementationMode.TEXTURE_VIEW
            imagePreview.setSurfaceProvider(previewView.createSurfaceProvider(camera.cameraInfo))
        }, ContextCompat.getMainExecutor(this))

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                previewView.post { startCamera() }
            } else {
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun takePicture() {


        imageCapture.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
            @SuppressLint("UnsafeExperimentalUsageError")
            override fun onCaptureSuccess(imageProxy: ImageProxy) {
                imageProxy.image?.let {
                    val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                    val bitmap = it.toBitmap(rotationDegrees)
                    runOnUiThread {
                        binding.apply {
                            previewView.visibility = View.INVISIBLE
                            capture.visibility= View.VISIBLE
                            capture.setImageBitmap(bitmap)
                        }

                    }


                    //super.onCaptureSuccess(imageProxy)
                }
            }
            override fun onError(exception: ImageCaptureException) {
                val msg = "Photo capture failed: ${exception.message}"
                previewView.post {
                    Toast.makeText(this@ScanActivity, msg, Toast.LENGTH_LONG).show()
                }
            }
        })
    }
    companion object {
        private const val TAG = "MainActivity"
        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PHOTO_EXTENSION = ".jpg"

        fun getOutputDirectory(context: Context): File {
            val appContext = context.applicationContext
            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
                File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() }
            }
            return if (mediaDir != null && mediaDir.exists())mediaDir else appContext.filesDir
        }

        fun createFile(baseFolder: File, format: String, extension: String) =
            File(baseFolder, SimpleDateFormat(format, Locale.US)
                .format(System.currentTimeMillis()) + extension)
    }



}
