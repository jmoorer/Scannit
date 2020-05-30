package com.moor.scannit.ui.ocr

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.moor.scannit.R
import com.moor.scannit.databinding.FragmentTextBinding
import com.moor.scannit.setClipboard


class TextFragment : Fragment() {


    val viewModel:OcrViewModel by activityViewModels()

    private val binding: FragmentTextBinding by lazy {
        FragmentTextBinding.bind(requireView())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_text, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getState().observe(viewLifecycleOwner, Observer { state->
            if (!state.isLoading){
                binding.apply {
                    textView.text=state.allText
                    copyButton.setOnClickListener {
                        requireContext().apply {
                            setClipboard(state.allText)
                            Toast.makeText(this,"Copied to Clipboard",Toast.LENGTH_SHORT).show()
                        }
                    }
                    downloadButton.setOnClickListener {
                        DownloadDialog(requireContext()).startDialog(state.allText)
                    }
                    shareButton.setOnClickListener {
                        shareText(state.allText)
                    }
                }
            }
        })
    }

    private fun shareText(text: String) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Ocr Text")
        sharingIntent.putExtra(Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(sharingIntent, "Share text via"))
    }

}
