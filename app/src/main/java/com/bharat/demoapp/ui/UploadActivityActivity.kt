package com.bharat.demoapp.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bharat.demoapp.R
import com.bharat.demoapp.databinding.ActivityUploadActivityBinding
import com.bharat.demoapp.ui.fragments.UploadMediaFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.File

class UploadActivityActivity : AppCompatActivity(), View.OnClickListener, UploadMediaFragment.SetOption {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityUploadActivityBinding

    var storage: FirebaseStorage? = null
    var storageReference: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storage = Firebase.storage
        storageReference = storage!!.reference
        auth = Firebase.auth

        binding.imageViewUpload.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        if(view == binding.imageViewUpload) {
            val fm = supportFragmentManager
            val bottomOptionsFragment = UploadMediaFragment.newInstance()
            bottomOptionsFragment.show(fm, "bottomSheet")
            return
        }
    }

    override fun onSetAttachment(filepath: String) {
        binding.imageViewUpload.isEnabled = false
        binding.imageViewUpload.setImageResource(R.drawable.ic_upload)
        binding.progressLoader.visibility = View.VISIBLE
        binding.textViewUploadStatus.text = "Uploading now..."
        uploadMedia(filepath)
    }

    private fun uploadMedia(filepath: String) {
        var file = Uri.fromFile(File(filepath))
        val mediaRef = storageReference!!.child("images/${auth.currentUser!!.uid}/${file.lastPathSegment}")
        val uploadTask = mediaRef.putFile(file)

        uploadTask.addOnFailureListener {
            binding.textViewUploadStatus.text = it.message
            binding.progressLoader.visibility = View.GONE
        }.addOnSuccessListener { taskSnapshot ->
            binding.textViewUploadStatus.text = "Upload success"
            binding.progressLoader.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, ViewPhotosActivity::class.java)
        startActivity(intent)
    }
}