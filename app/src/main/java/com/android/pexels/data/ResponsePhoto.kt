package com.android.pexels.data

import com.android.pexels.data.cache.Photo
import com.google.gson.annotations.SerializedName

data class PexelPage(@SerializedName("photos") val responsePhotos: List<ResponsePhoto>)

public data class ResponsePhoto(
    var id: Int,
    var width: Int,
    var height: Int,
    var url: String, @SerializedName("src") val urlsResponse: ResponsePhotoUrls
) {
    fun toPhoto() = Photo(
        id,
        width,
        height,
        url,
        urlsResponse.original,
        urlsResponse.large2x,
        urlsResponse.large,
        urlsResponse.medium,
        urlsResponse.small,
        urlsResponse.tiny,
        urlsResponse.portrait
    )
}

public data class ResponsePhotoUrls(
    val original: String?,
    val large2x: String?,
    val large: String?,
    val medium: String?,
    val small: String?,
    val tiny: String?,
    val portrait: String?
)
