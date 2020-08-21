package com.android.pexels.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.pexels.api.PhotoService
import com.android.pexels.data.cache.Photo
import com.android.pexels.data.cache.PhotoDAO

/**
 * Repository class managing the network and database.
 */
class PhotoRepository(var imageService: PhotoService, val photoDAO: PhotoDAO) {

    fun loadPhotos(
        page: Int,
        photoCount: Int,
        imageLoadError: MutableLiveData<String>
    ): LiveData<List<Photo>> {
        // Update the cache with fresh contents in background.
        imageService.getCuratedImageList(page, photoCount, imageLoadError)
            .observeForever { photoList ->
                Thread {
                    photoList.map {
                        it.toPhoto()
                    }.also {
                        photoDAO.insertPhotos(it)
                    }
                }.start()
            }
        return photoDAO.getPhotos(photoCount, page * photoCount)
    }
}