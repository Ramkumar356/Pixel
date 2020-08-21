package com.android.mygithub.adapters

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.android.pexels.R
import com.android.pexels.utilities.ImageLoader
import com.android.pexels.data.cache.Photo
import com.android.pexels.network.Callback

/**
 * Adapter for [RecyclerView] in [com.android.pexels.HomeActivity]
 */
class PhotoListAdapter(var photoList: ArrayList<Photo>) :
    RecyclerView.Adapter<PhotoListAdapter.ImageViewHolder>() {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return photoList[position].id.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context);
        return ImageViewHolder(layoutInflater.inflate(R.layout.list_item_photo, parent, false))
    }

    override fun getItemCount() = photoList.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(photoList[position])
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageView: ImageView = itemView.findViewById(R.id.pexelPhoto);
        val progressLayout: FrameLayout = itemView.findViewById(R.id.progressContainer)

        fun bind(photo: Photo) {
            progressLayout.visibility = VISIBLE

            ImageLoader.loadImageInto(imageView, photo, object :
                Callback<Bitmap?> {
                override fun onSuccess(response: Bitmap?) {
                    response?.run {
                        imageView.post {
                            imageView.setImageBitmap(response)
                            progressLayout.visibility = View.GONE
                            imageView.invalidate()
                            progressLayout.invalidate()
                        }
                    }
                }

                override fun onError(errorMessage: String) {
                    imageView.post {
                        progressLayout.visibility = View.GONE
                        progressLayout.invalidate()
                    }

                }
            })
            progressLayout.invalidate()
        }

    }
}
