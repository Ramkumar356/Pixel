package com.android.pexels.data

import com.google.gson.annotations.SerializedName

data class PexelPage(val photos: List<Photo>)

data class Photo(
    var id: Int,
    var width: Int,
    var height: Int,
    var url: String, @SerializedName("src") val urls: PhotoUrls
)

public data class PhotoUrls(
    val original: String?,
    val large2x: String?,
    val large: String?,
    val medium: String?,
    val small: String?,
    val tiny: String?,
    val portrait: String?
)