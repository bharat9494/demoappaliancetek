package com.bharat.demoapp.misc

import android.content.Context
import android.util.Log
import android.webkit.MimeTypeMap
import java.io.File
import java.io.IOException


fun getMimeType(url: String?): String? {
    var mimeType: String? = null
    val extension = MimeTypeMap.getFileExtensionFromUrl(url)
    if (extension != null) {
        mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }
    return mimeType
}

fun Context.getAllDownloadedFiles(): ArrayList<FirebaseMediaFile> {

    val files: ArrayList<FirebaseMediaFile> = ArrayList()
    val path = File(getExternalFilesDir(null)!!.absolutePath)
    if (path.exists()) {

        for (i in path.list()!!) {
            if(i.endsWith(mp4)) {
                val mediaFile = FirebaseMediaFile(i, "$path/$i", video)
                files.add(mediaFile)
            } else if(i.endsWith(jpg)) {
                val mediaFile = FirebaseMediaFile(i, "$path/$i", image)
                files.add(mediaFile)
            }
        }
    }

    //Log.i("TAG", "getAllDownloadedFiles: ${files.map { x -> x.name }.joinToString(",")}")
    return files
}

fun isOnline(): Boolean {
    val runtime = Runtime.getRuntime()
    try {
        val ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8")
        val exitValue = ipProcess.waitFor()
        return exitValue == 0
    } catch (e: IOException) {
        e.printStackTrace()
    } catch (e: InterruptedException) {
        e.printStackTrace()
    }
    return false
}