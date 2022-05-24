package com.bharat.demoapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bharat.demoapp.R
import com.bharat.demoapp.databinding.ActivityViewPhotosBinding
import com.bharat.demoapp.misc.FirebaseMediaFile
import com.bharat.demoapp.misc.getAllDownloadedFiles
import com.bharat.demoapp.misc.isOnline
import com.bharat.demoapp.ui.adapters.MediaAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class ViewPhotosActivity : AppCompatActivity(){
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityViewPhotosBinding

    var imagelist: ArrayList<FirebaseMediaFile>? = null
    var root: StorageReference? = null
    var adapter: MediaAdapter? = null
    var storageRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewPhotosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        storageRef = FirebaseStorage.getInstance().reference.child("media/${auth.currentUser!!.uid}")

        imagelist = ArrayList()
        adapter = MediaAdapter(object : MediaAdapter.OnItemClickListener{
            override fun onDownload(firebaseMediaFile: FirebaseMediaFile) {
                Toast.makeText(this@ViewPhotosActivity, "Downloading..", Toast.LENGTH_SHORT).show()
                downloadAndShare(firebaseMediaFile)
            }

            override fun onShare(firebaseMediaFile: FirebaseMediaFile) {
                downloadAndShare(firebaseMediaFile, true)
            }

            override fun onView(firebaseMediaFile: FirebaseMediaFile) {
                //not implemented
            }
        }, this)
        binding.progress.visibility = View.VISIBLE

        if(isOnline()) {
            binding.textViewOffline.visibility = View.GONE
            val listRef = storageRef!!
            listRef.listAll()
                .addOnFailureListener {
                    Log.i("TAG", "onCreate: ${it.message}")
                }
                .addOnSuccessListener { listResult ->
                    for (file in listResult.items) {
                        file.downloadUrl.addOnSuccessListener { uri ->

                            val type = if(file.name.endsWith(".mp4")) {
                                "Video"
                            } else {
                                "Image"
                            }

                            imagelist!!.add(
                                FirebaseMediaFile(file.name, uri.toString(), type)
                            )
                        }.addOnSuccessListener {
                            binding.recyclerview.adapter = adapter
                            adapter!!.setMediaList(imagelist)
                        }
                    }

                    if(listResult.items.isEmpty()) {
                        binding.noData.visibility = View.VISIBLE
                    } else {
                        binding.noData.visibility = View.GONE
                    }
                }
                .addOnCompleteListener {
                    Log.i("TAG", "onCreate: ${it.exception}")
                    binding.progress.visibility = View.GONE
                }
        } else {
            binding.textViewOffline.visibility = View.VISIBLE
            val listOfFiles = this.getAllDownloadedFiles()
            binding.progress.visibility = View.GONE
            adapter!!.setMediaList(listOfFiles)
            binding.recyclerview.adapter = adapter

            if(listOfFiles.isEmpty()) {
                binding.noData.visibility = View.VISIBLE
            } else {
                binding.noData.visibility = View.GONE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.action_upload){
            val intent = Intent(this, UploadActivityActivity::class.java)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }

    fun downloadAndShare(firebaseMediaFile: FirebaseMediaFile, share: Boolean = false, view: Boolean = false) {
        val imgRef = storageRef!!.child(firebaseMediaFile.name)
        val localFile = File(getExternalFilesDir(null)!!.absolutePath, firebaseMediaFile.name)

        imgRef.getFile(localFile).addOnSuccessListener {
            Toast.makeText(this@ViewPhotosActivity, "success", Toast.LENGTH_SHORT).show()

            if(share) {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "image/*"
                shareIntent.putExtra(
                    Intent.EXTRA_STREAM,
                    FileProvider.getUriForFile(
                        this,
                        resources.getString(R.string.content_provider),
                        localFile
                    )
                )
                startActivity(Intent.createChooser(shareIntent, "Share via"))
            }

        }.addOnFailureListener {
            Toast.makeText(this@ViewPhotosActivity, it.message, Toast.LENGTH_SHORT).show()
        }
    }
}