package com.moor.scannit.ui.ocr

import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import java.util.regex.Matcher

class OcrViewModel:ViewModel() {

    private var detector = FirebaseVision.getInstance().onDeviceTextRecognizer

    private  val state = MutableLiveData<ViewState>()

    data class ViewState(
        val isLoading :Boolean=false,
        val allText:String="",
        val numbers:List<String> = emptyList(),
        val emails:List<String> = emptyList(),
        val links:List<String> = emptyList(),
        var error:String?="",
        val boxes:List<Rect> = emptyList(),
        val bitmap: Bitmap?=null
    )

    fun getState():LiveData<ViewState> = state

    fun  extractText(image: FirebaseVisionImage): LiveData<ViewState> {
        return state.apply {
            value= ViewState(isLoading = true)
            detector.processImage(image)
                .addOnSuccessListener { result ->

                    value = ViewState(
                        isLoading = false,
                        allText = result.text,
                        numbers = extractPhoneNumbers(result.text),
                        emails = extractEmails(result.text),
                        links = extractLinks(result.text),
                        boxes = result.textBlocks.flatMap { it.lines }.flatMap { it.elements }.map { it.boundingBox!! },
                        bitmap = image.bitmap
                    )
                }
                .addOnFailureListener { e ->
                    value = ViewState(
                        isLoading = false,
                        error =  e.message
                    )
                }
        }
    }

    fun clearState(){
        state.value= ViewState()
    }

    private  fun extractPhoneNumbers(text:String): ArrayList<String> {

        val phoneNumbers= arrayListOf<String>()
        val regexMatcher: Matcher = Patterns.PHONE.matcher(text)
        while(regexMatcher.find()) {
            val number=regexMatcher.group()
            if(number.replace("-","").length in 7..12){
                phoneNumbers.add(number)
            }
        }
        return phoneNumbers
    }

    private  fun extractEmails(text:String): ArrayList<String> {

        val emails= arrayListOf<String>()
        val regexMatcher: Matcher = Patterns.EMAIL_ADDRESS.matcher(text)
        while(regexMatcher.find()) {
            val number=regexMatcher.group()
            emails.add(number)
        }
        return emails
    }

    private  fun extractLinks(text:String): ArrayList<String> {

        val links= arrayListOf<String>()
        val regexMatcher: Matcher = Patterns.WEB_URL.matcher(text)
        while(regexMatcher.find()) {
            val number=regexMatcher.group()
            links.add(number)
        }
        return links
    }
}