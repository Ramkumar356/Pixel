package com.android.pexels.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.pexels.data.PexelPage
import com.android.pexels.data.Photo
import com.android.pexels.network.Callback
import com.android.pexels.network.JsonHttpsRequest
import com.android.pexels.network.JsonRequest
import com.android.pexels.utilities.HTTP_HEADER_AUTHORIZATION
import com.android.pexels.utilities.PEXEL_API_KEY
import com.android.pexels.utilities.PEXEL_BASE_URL
import com.google.gson.Gson
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

val PEXEL_AUTH_HEADER = Pair(HTTP_HEADER_AUTHORIZATION, PEXEL_API_KEY)

val jsonExecutors: ExecutorService = Executors.newCachedThreadPool()

class PexelPhotoService {

    fun getCuratedImageList(page: Int, count: Int): LiveData<List<Photo>> {
        val photoData: MutableLiveData<List<Photo>> = MutableLiveData()
        val url = "${PEXEL_BASE_URL}?page=${page + 1}&per_page=${count}"
        JsonHttpsRequest(
            JsonRequest(url, mapOf(PEXEL_AUTH_HEADER)), jsonExecutors,
            object : Callback<String> {
                override fun onSuccess(response: String) {
                    response.run {
                        val photos = Gson().fromJson(this, PexelPage::class.java).photos
                        photoData.postValue(photos)
                    }
                }

                override fun onError(errorMessage: String) {
                    photoData.postValue(emptyList())
                }
            }
        ).execute()
        return photoData
    }
}

