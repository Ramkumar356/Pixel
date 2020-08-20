package com.android.pexels.data

import androidx.lifecycle.LiveData
import com.android.pexels.api.PexelPhotoService
import com.android.pexels.data.cache.PhotoDAO
import com.android.pexels.data.cache.PhotoEntity

class PhotoRepository(var imageService: PexelPhotoService, val photoDAO: PhotoDAO) {

    fun loadPhotos(page: Int, photoCount: Int): LiveData<List<PhotoEntity>> {

        imageService.getCuratedImageList(page,photoCount).observeForever { photoList ->

            val photoEntityList: List<PhotoEntity> = photoList.map {
                PhotoEntity(
                    it.id,
                    it.width,
                    it.height,
                    it.url,
                    it.urls.original,
                    it.urls.large2x,
                    it.urls.large,
                    it.urls.medium,
                    it.urls.small,
                    it.urls.tiny,
                    it.urls.portrait
                )
            }
            Thread{
                photoDAO.updatePhotos(photoEntityList)
            }.start()
        }
        return photoDAO.getPhotos(photoCount, page * photoCount)
    }

}