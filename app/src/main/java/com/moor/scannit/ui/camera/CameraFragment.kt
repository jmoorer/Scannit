package com.moor.scannit.ui.camera

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
import com.moor.scannit.databinding.FragmentCameraBinding
import com.moor.scannit.supportActionBar
import com.moor.scannit.toBitmap
import java.io.File
import java.io.FileOutputStream


class CameraFragment() : Fragment() {
    private lateinit var imageAnalyzer: ImageAnalysis
    private lateinit var imageCapture: ImageCapture

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var binding: FragmentCameraBinding

    private lateinit var imagePreview: Preview
    private lateinit var previewView: PreviewView

    private val REQUEST_CODE_PERMISSIONS = 10
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

    private lateinit var cameraControl: CameraControl
    private lateinit var cameraInfo: CameraInfo


    private val viewModel:CameraViewModel by activityViewModels()
    private val args:CameraFragmentArgs by navArgs()
    var menu: Menu?=null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        if(args.documentId>0)
            viewModel.setDocument(args.documentId)
        setHasOptionsMenu(true)

    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        binding = FragmentCameraBinding.inflate(inflater, container, false).apply {
            this@CameraFragment.previewView = previewView
            cameraCaptureButton.setOnClickListener {
                takePicture()
            }

            flashCheckbox.setOnCheckedChangeListener { _, isChecked ->
                imageCapture.flashMode= if (isChecked) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF
            }

            modesRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                when(checkedId){
                    R.id.single_radio-> CameraViewModel.CameraMode.Single
                    R.id.batch_radio-> CameraViewModel.CameraMode.Batch
                    else-> CameraViewModel.CameraMode.None
                }.let {
                    viewModel.setMode(it)
                }
            }
            modesRadioGroup.check(R.id.single_radio)

            batchSaveButton.setOnClickListener {
                val id = viewModel.saveBatch()
                val action= CameraFragmentDirections.actionCameraFragmentToDocumentFragment(id)
                findNavController().navigate(action)
            }
        }
        if (allPermissionsGranted()) {
            previewView.post { startCamera() }
        } else {
            requestPermissions(
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }


        return binding.root
    }

    @SuppressLint("RestrictedApi")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getState().observe(viewLifecycleOwner, Observer { state->
            when(state.cameraMode){
                CameraViewModel.CameraMode.Batch->{
                    binding.apply {
                        batchFrame.visibility=View.VISIBLE
                        if(state.batchBitmaps.any()){
                            batchSaveButton.visibility=View.VISIBLE
                            pageCountTextView.visibility=View.VISIBLE
                            previewImageView.setImageBitmap(state.batchBitmaps.last())
                            pageCountTextView.text= "${state.batchBitmaps.size}"
                        }else{
                            previewImageView.setImageDrawable(null)
                            pageCountTextView.visibility=View.INVISIBLE
                            batchSaveButton.visibility=View.INVISIBLE
                        }

                    }
                }
                else ->  binding.apply {
                    batchSaveButton.visibility=View.GONE
                    batchFrame.visibility=View.GONE
                }
            }
        })
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
                        when(viewModel.mode){
                            CameraViewModel.CameraMode.Single->{
                                viewModel.setImage(bitmap)
                                findNavController().navigate(R.id.cropFragment)
                            }
                            CameraViewModel.CameraMode.Batch->{
                                viewModel.addBatchImage(bitmap)

                            }
                        }
                    }

                    super.onCaptureSuccess(imageProxy)
                }
            })
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                previewView.post { startCamera() }
            } else {
                findNavController().popBackStack()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("RestrictedApi")
    private fun startCamera() {
        imageCapture = ImageCapture.Builder().apply {
            setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
        }.build()

        imagePreview = Preview.Builder().apply {
            setTargetAspectRatio(AspectRatio.RATIO_16_9)
            setTargetRotation(previewView.display.rotation)
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
            imagePreview.setSurfaceProvider(previewView.createSurfaceProvider(camera.cameraInfo))
            val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    val currentZoomRatio: Float = cameraInfo.zoomState.value?.zoomRatio ?: 0F
                    val delta = detector.scaleFactor
                    cameraControl.setZoomRatio(currentZoomRatio * delta)
                    return true
                }
            }

            val scaleGestureDetector = ScaleGestureDetector(context, listener)

            previewView.setOnTouchListener { v, event ->
                scaleGestureDetector.onTouchEvent(event)
                return@setOnTouchListener true
            }
        }, ContextCompat.getMainExecutor(requireContext()))

    }

    @SuppressLint("RestrictedApi")
    override fun onDestroyView() {
        super.onDestroyView()
        CameraX.unbindAll()
    }

}
