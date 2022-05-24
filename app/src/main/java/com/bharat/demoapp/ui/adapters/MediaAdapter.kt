package com.bharat.demoapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.bharat.demoapp.R
import com.bharat.demoapp.misc.FirebaseMediaFile
import com.bharat.demoapp.misc.image
import com.bumptech.glide.Glide


class MediaAdapter(clickListener: OnItemClickListener, val mContext: Context) : RecyclerView.Adapter<MediaAdapter.ViewHolder>() {

    private val mListener: OnItemClickListener = clickListener
    private var mMediaList: ArrayList<FirebaseMediaFile>? = null
    private var mIsLocal: Boolean = false

    fun setMediaList(mediaList: ArrayList<FirebaseMediaFile>?) {
        this.mMediaList = mediaList
        notifyDataSetChanged()
    }

    fun setIsLocal(isLocal: Boolean) {
        this.mIsLocal = isLocal
    }

    override fun getItemViewType(position: Int): Int {
        return when(mMediaList!![position].type) {
            image -> 1
            else -> 2
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaAdapter.ViewHolder {
        return ViewHolder.from(parent, viewType)
    }

    override fun onBindViewHolder(holder: MediaAdapter.ViewHolder, position: Int) {
        val item = mMediaList?.get(position)
        holder.bind(item, mListener)
    }

    override fun getItemCount(): Int {
        return mMediaList?.size ?: 0
    }

    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val imageView: ImageView? = itemView.findViewById(R.id.item)
        private val videoView: VideoView? = itemView.findViewById(R.id.videoItem)

        private val textviewDownload: TextView = itemView.findViewById(R.id.textviewDownload)
        private val textviewView: TextView = itemView.findViewById(R.id.textviewView)
        private val textviewShare: TextView = itemView.findViewById(R.id.textviewShare)

        fun bind(item: FirebaseMediaFile?, onItemClickListener: OnItemClickListener) {
            //val res = itemView.context.resources
            if(imageView != null) {
                Glide.with(imageView.context).load(item!!.url).into(imageView)
            } else {
                videoView?.setVideoPath(item!!.url)
                videoView?.seekTo(1)
            }

            imageView?.setOnClickListener {
                onItemClickListener.onItemClick(item!!)
            }
            videoView?.setOnClickListener {
                onItemClickListener.onItemClick(item!!)
            }
            textviewDownload.setOnClickListener {
                onItemClickListener.onDownload(item!!)
            }
            textviewView.setOnClickListener {
                onItemClickListener.onView(item!!)
            }
            textviewShare.setOnClickListener {
                onItemClickListener.onShare(item!!)
            }
        }

        companion object {
            fun from(parent: ViewGroup, viewType: Int): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view =  when(viewType) {
                    1 -> layoutInflater.inflate(R.layout.image_item, parent, false)
                    else -> layoutInflater.inflate(R.layout.video_item, parent, false)
                }

                return ViewHolder(view)
            }
        }
    }

    interface OnItemClickListener {
        fun onDownload(firebaseMediaFile: FirebaseMediaFile)
        fun onShare(firebaseMediaFile: FirebaseMediaFile)
        fun onView(firebaseMediaFile: FirebaseMediaFile)
        fun onItemClick(firebaseMediaFile: FirebaseMediaFile)
    }

}