package com.bharat.demoapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bharat.demoapp.R
import com.bharat.demoapp.misc.FirebaseMediaFile
import com.bumptech.glide.Glide


class ImageAdapter(clickListener: OnItemClickListener, val mContext: Context) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    private val mListener: OnItemClickListener = clickListener
    private var mImageList: ArrayList<FirebaseMediaFile>? = null

    fun setImageList( imageList: ArrayList<FirebaseMediaFile>?) {
        this.mImageList = imageList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageAdapter.ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ImageAdapter.ViewHolder, position: Int) {
        val item = mImageList?.get(position)
        holder.bind(item, mListener)
    }

    override fun getItemCount(): Int {
        return mImageList?.size ?: 0
    }

    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val imageView: ImageView = itemView.findViewById(R.id.item)
        private val textviewDownload: TextView = itemView.findViewById(R.id.textviewDownload)
        private val textviewView: TextView = itemView.findViewById(R.id.textviewView)
        private val textviewShare: TextView = itemView.findViewById(R.id.textviewShare)

        fun bind(item: FirebaseMediaFile?, onItemClickListener: OnItemClickListener) {
            //val res = itemView.context.resources
            Glide.with(imageView.context).load(item!!.url).into(imageView)

            textviewDownload.setOnClickListener {
                onItemClickListener.onDownload(item)
            }
            textviewView.setOnClickListener {
                onItemClickListener.onView(item)
            }
            textviewShare.setOnClickListener {
                onItemClickListener.onShare(item)
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater
                    .inflate(R.layout.image_item, parent, false)

                return ViewHolder(view)
            }
        }
    }

    interface OnItemClickListener {
        fun onDownload(firebaseMediaFile: FirebaseMediaFile)
        fun onShare(firebaseMediaFile: FirebaseMediaFile)
        fun onView(firebaseMediaFile: FirebaseMediaFile)
    }

}