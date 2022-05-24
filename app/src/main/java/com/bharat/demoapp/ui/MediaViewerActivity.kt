package com.bharat.demoapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bharat.demoapp.R
import com.bharat.demoapp.databinding.ActivityMediaViewerBinding
import com.bharat.demoapp.databinding.ActivityViewPhotosBinding
import com.bharat.demoapp.misc.emptyString
import com.bharat.demoapp.misc.image
import com.bharat.demoapp.misc.type
import com.bharat.demoapp.misc.url
import com.bumptech.glide.Glide

class MediaViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaViewerBinding

    var mediaUrl: String = emptyString
    var mediaType: String = emptyString

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mediaUrl = intent.getStringExtra(url) ?: emptyString
        mediaType = intent.getStringExtra(type) ?: emptyString

        if(mediaType == image) {
            binding.imageViewViewer.visibility = View.VISIBLE
            binding.videoViewViewer.visibility = View.GONE
            Glide.with(this).load(mediaUrl).into(binding.imageViewViewer)
        } else {
            binding.imageViewViewer.visibility = View.GONE
            binding.videoViewViewer.visibility = View.VISIBLE
            binding.videoViewViewer.setVideoPath(mediaUrl)
            binding.videoViewViewer.seekTo(1)
            binding.videoViewViewer.start()
        }

    }
}