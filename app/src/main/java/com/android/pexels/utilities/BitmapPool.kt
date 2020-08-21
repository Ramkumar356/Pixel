package com.android.pexels.utilities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.LruCache
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest

/**
 * Pool of used [Bitmap] that can be reused when decoding new bitmap.
 * Which helps to avoid running GC frequently causing rendering performance issues.
 */
object BitmapPool {

    private val lruCache: LruCache<BitmapKey, Bitmap> = LruCache(40 * 1024 * 1024)

    fun getBitMap(height: Int, width: Int): Bitmap? {
        val bitmap: Bitmap? = lruCache[BitmapKey(height, width)]
        bitmap?.also {
            lruCache.remove(BitmapKey(height, width))
        }
        return bitmap
    }

    fun put(bitmap: Bitmap) {
        lruCache.put(BitmapKey(bitmap.height, bitmap.width), bitmap)
    }
}

/**
 *  Tells whether [cachedBitmap] can be used in place which decoding [Bitmap] with [bitmapOptions]
 */
fun canUserForInBitmap(
    cachedBitmap: Bitmap, bitmapOptions: BitmapFactory.Options
): Boolean {
    val width = bitmapOptions.outWidth / bitmapOptions.inSampleSize
    val height = bitmapOptions.outHeight / bitmapOptions.inSampleSize
    val byteCount: Int = width * height * getBytesPerPixel(cachedBitmap.config)
    return try {
        byteCount <= cachedBitmap.allocationByteCount
    } catch (e: NullPointerException) {
        byteCount <= cachedBitmap.height * cachedBitmap.rowBytes
    }
}

private fun getBytesPerPixel(config: Bitmap.Config): Int {
    var bitMapConfig: Bitmap.Config? = config
    bitMapConfig = bitMapConfig ?: Bitmap.Config.ARGB_8888
    return when (bitMapConfig) {
        Bitmap.Config.ALPHA_8 -> 1
        Bitmap.Config.RGB_565, Bitmap.Config.ARGB_4444 -> 2
        Bitmap.Config.ARGB_8888 -> 4
        else -> 4
    }
}

fun String.toMD5(): String {
    val bytes = MessageDigest.getInstance("MD5").digest(this.toByteArray())
    return bytes.toHex()
}

fun ByteArray.toHex(): String {
    return joinToString("") { "%02x".format(it) }
}

/**
 * Cache the image into local file system.
 */
fun saveImage(context: Context, url: String, bitMap: Bitmap) {
    val cacheDir =
        File("${context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()}/cache/")
    if (!cacheDir.exists()) {
        cacheDir.mkdirs()
    }
    val imageFile = File(cacheDir, "${url.toMD5()}.jpeg")
    if (imageFile.exists()) {
        return
    } else {
        imageFile.createNewFile()
    }
    try {
        FileOutputStream(imageFile).run {
            bitMap.compress(Bitmap.CompressFormat.JPEG, 90, this)
            flush()
            close()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

data class BitmapKey(var height: Int, var width: Int)