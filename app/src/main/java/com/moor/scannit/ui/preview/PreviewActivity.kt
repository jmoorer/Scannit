package com.moor.scannit.ui.preview

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.moor.scannit.R
import com.moor.scannit.databinding.ActivityPreviewBinding
import com.moor.scannit.ui.preview.dummy.DummyContent

class PreviewActivity : AppCompatActivity(),
    ExtractedTextFragment.OnListFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding= ActivityPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val intent = getIntent();
//        val path= intent.getStringExtra("image");
//        val fileUri = Uri.parse(path)
//        findViewById<ImageView>(R.id.imageView).apply {
//            setImageURI(fileUri)
//        }

        binding.viewPager.adapter= ViewPagerAdapater(supportFragmentManager)
    }

    inner class  ViewPagerAdapater(mangager:FragmentManager) : FragmentStatePagerAdapter(mangager){
        override fun getItem(position: Int): Fragment {
           val uri= intent.getStringExtra("image");

           return when(position){
               0-> ImageFragment.newInstance(uri)
               1-> ExtractedTextFragment.newInstance(uri)
               else -> TODO()
           }
        }

        override fun getCount() = 2

    }

    override fun onListFragmentInteraction(item: DummyContent.DummyItem?) {
        TODO("Not yet implemented")
    }
}
