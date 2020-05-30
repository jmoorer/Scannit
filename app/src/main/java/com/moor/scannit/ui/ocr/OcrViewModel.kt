package com.moor.scannit.ui.ocr

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer
import java.lang.Exception

class OcrViewModel:ViewModel() {

    private var detector = FirebaseVision.getInstance().onDeviceTextRecognizer

    private  val state = MutableLiveData<ViewState>()

    data class ViewState(
        val isLoading :Boolean=false,
        val allText:String="",
        val numbers:List<String> = emptyList(),
        val emails:List<String> = emptyList(),
        val links:List<String> = emptyList(),
        var error:String?=""
    )

    fun getState():LiveData<ViewState> = state

    fun  extractText(image: FirebaseVisionImage): LiveData<ViewState> {
        return state.apply {
            value= ViewState(isLoading = true)
            detector.processImage(image)
                .addOnSuccessListener { result ->
                    value= ViewState(isLoading = false,allText = result.text)
                }
                .addOnFailureListener { e ->

                }
                .addOnCompleteListener {

                }
        }
    }
}