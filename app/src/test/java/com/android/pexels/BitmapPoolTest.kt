package com.android.pexels

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.android.pexels.utilities.canUseForInBitmap
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class BitmapPoolTest {

    @Mock
    lateinit var bitMap: Bitmap

    @Test
    fun testBitmapPool() {
        Mockito.`when`(bitMap.allocationByteCount).thenReturn(1000000)
        Mockito.`when`(bitMap.config).thenReturn(Bitmap.Config.ARGB_8888)
        val bitmapOptionsCompat = BitmapFactory.Options().apply {
            inSampleSize = 1
            outConfig
            outHeight = 100
            outWidth = 100
        }
        assertTrue(canUseForInBitmap(bitMap, bitmapOptionsCompat))
        Mockito.`when`(bitMap.allocationByteCount).thenReturn(100)
        assertFalse(canUseForInBitmap(bitMap, bitmapOptionsCompat))
    }
}