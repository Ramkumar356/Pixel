package com.android.pexels

import android.app.Application
import com.android.pexels.utilities.ImageLoader

public class PexelApplication : Application(){

    override fun onCreate() {
        super.onCreate()
        ImageLoader.init(this)
    }

}