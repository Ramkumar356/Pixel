package com.android.pexels.network

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import com.android.pexels.utilities.*
import java.io.File
import java.net.URL
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import javax.net.ssl.HttpsURLConnection

/**
 * Performs loading images from cache/network and decode using BitmapPool.
 */
class ImageDownloadRequest(
    val context: Context,
    var imageUrl: String,
    var executor: ExecutorService,
    var callback: Callback<Bitmap?>,
    private val width: Int,
    private val height: Int
) : Callable<Bitmap?> {

    fun execute(): Future<Bitmap?> = executor.submit(this)

    override fun call(): Bitmap? {
        // Fetch and decode if the image is already cached.
        if (getFileFromCache(context, imageUrl).exists()) {
            decodeBitmapFromFile(getFileFromCache(context, imageUrl)).run {
                callback.onSuccess(this)
                return this
            }
        }
        val url = URL(imageUrl)
        var httpsConnection: HttpsURLConnection? = null
        try {
            httpsConnection = (url.openConnection() as HttpsURLConnection).also {
                it.addRequestProperty(HTTP_HEADER_AUTHORIZATION, PEXEL_API_KEY)
                it.requestMethod = GET_HTTP_REQUEST_METHOD
                it.connect()
                val bitmap = decodeBitmap(url)
                saveImage(context, imageUrl, bitmap!!)
                callback.onSuccess(bitmap)
            }
        } catch (e: Exception) {
            httpsConnection?.disconnect()
            e.printStackTrace()
        } finally {
            httpsConnection?.disconnect()
        }
        return null
    }

    /**
     * Decode the bitmap from [InputStream] using BitmapPool.
     */
    private fun decodeBitmap(url: URL): Bitmap? {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeStream(url.openStream(), null, options)
        options.inMutable = true
        options.inSampleSize = calculateInSampleSize(options, width, height)
        // Get cached bitmap from pool matching the required width and height
        val inBitmap: Bitmap? = BitmapPool.getBitMap(
            options.outHeight,
            options.outWidth
        )
        // Enable options.inBitmap if cached bitmap memory can be used in place of new image.
        if (inBitmap != null && canUserForInBitmap(inBitmap, options)) {
            options.inBitmap = inBitmap
        }
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeStream(
            url.openStream(),
            null,
            options
        )
    }

    /**
     * Fetch and decode Bitmap from local file system using BitmapPool.
     */
    private fun decodeBitmapFromFile(file: File): Bitmap? {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(file.path, options)
        options.inMutable = true
        options.inSampleSize = calculateInSampleSize(options, width, height)
        val inBitmap: Bitmap? = BitmapPool.getBitMap(
            options.outHeight,
            options.outWidth
        )
        // Enable options.inBitmap if cached bitmap memory can be used in place of new image.
        if (inBitmap != null && canUserForInBitmap(inBitmap, options)) {
            options.inBitmap = inBitmap
        }
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(
            file.path,
            options
        )
    }

    // Calculate the BitmapOption.InsSampleSize for decoding bitmap to required width and height.
    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}

// Utils to get file object of cached image.
fun getFileFromCache(context: Context, url: String): File {
    val cacheDir =
        File("${context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()}/cache/")
    return File(cacheDir, "${url.toMD5()}.jpeg")
}
