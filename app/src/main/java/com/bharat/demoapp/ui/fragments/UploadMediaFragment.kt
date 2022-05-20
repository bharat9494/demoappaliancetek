package com.bharat.demoapp.ui.fragments

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import com.bharat.demoapp.R
import com.bharat.demoapp.databinding.FragmentUploadMediaBinding
import com.bharat.demoapp.misc.REQUEST_TAKE_PHOTO
import com.bharat.demoapp.misc.REQUEST_VIDEO_CAPTURE
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class UploadMediaFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentUploadMediaBinding
    var currentMediaPath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUploadMediaBinding.inflate(layoutInflater)
        val listener = activity as SetOption

        binding.btnAddPhoto.setOnClickListener {
            dispatchTakePictureIntent()
        }

        binding.btnAddVideo.setOnClickListener {
            dispatchTakeVideoIntent()
        }

        binding.fabSetAttachment.setOnClickListener {
            listener.onSetAttachment(currentMediaPath)
            dismiss()
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            galleryAddPic()
            Glide.with(this).load(currentMediaPath).into(binding.imageViewAttachment)
            binding.btnAddPhoto.text = "Click Another"
            binding.fabSetAttachment.visibility = View.VISIBLE
            binding.videoView.visibility = View.GONE
            binding.imageViewAttachment.visibility = View.VISIBLE
        }

        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == Activity.RESULT_OK) {
            val videoUri: Uri = data!!.data!!
            currentMediaPath = videoUri.path!!

            binding.videoView.setVideoURI(videoUri)
            binding.videoView.visibility = View.VISIBLE
            binding.imageViewAttachment.visibility = View.GONE

            binding.btnAddPhoto.text = "Record Another"
            binding.fabSetAttachment.visibility = View.VISIBLE
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun galleryAddPic() {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val f = File(currentMediaPath)
        val contentUri = Uri.fromFile(f)
        mediaScanIntent.data = contentUri
        activity?.sendBroadcast(mediaScanIntent)
    }

    private fun dispatchTakeVideoIntent() {
        Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { takeVideoIntent ->
            takeVideoIntent.resolveActivity(requireActivity().packageManager)?.also {
                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE)
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
                if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
                    // Create the File where the photo should go
                    var photoFile: File? = null
                    try {
                        photoFile = createImageFile()
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        val photoURI = FileProvider.getUriForFile(
                            requireActivity(),
                            resources.getString(R.string.content_provider),
                            photoFile
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(
                            takePictureIntent,
                            REQUEST_TAKE_PHOTO
                        )
                    }
                }
            }
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(activity, "No Camera found", Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir: File? = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )

        currentMediaPath = image.absolutePath
        return image
    }

    companion object {
        @JvmStatic
        fun newInstance() = UploadMediaFragment()
    }

    internal interface SetOption {
        fun onSetAttachment(filepath: String)
    }
}