package com.android.pexels.utilities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import com.android.pexels.data.cache.Photo
import com.android.pexels.network.Callback
import com.android.pexels.network.ImageDownloadRequest
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

object ImageLoader {

    // Executor service for downloading the images.
    private val imageExecutors: ExecutorService = Executors.newCachedThreadPool()
    // Map of ImageView and Future object of active Callable object doing image fetch, cache and decode.
    private val requestTracker: MutableMap<ImageView, Future<Bitmap?>> = mutableMapOf()
    // Reference of application context. Should be initialized in application class.
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context
    }

    fun loadImageInto(
        imageView: ImageView,
        photo: Photo,
        callback: Callback<Bitmap?>
    ) {
        // Cache the existing Bitmap from imageview into BitmapPool.
        if (imageView.drawable is BitmapDrawable) {
            (imageView.drawable as BitmapDrawable).bitmap?.also {
                BitmapPool.put(it)
            }
        }
        // Cancels the existing active request for the image view.
        requestTracker[imageView]?.also {
            it.cancel(true)
        }

        ImageDownloadRequest(
            appContext,
            photo.medium!!,
            imageExecutors,
            callback,
            imageView.drawable.intrinsicWidth,
            imageView.drawable.intrinsicHeight
        ).execute().run {
            requestTracker[imageView] = this
        }
    }
}

