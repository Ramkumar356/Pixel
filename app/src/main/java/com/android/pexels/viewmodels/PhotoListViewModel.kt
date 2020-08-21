package com.android.pexels.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.android.pexels.api.PhotoService
import com.android.pexels.data.PhotoRepository
import com.android.pexels.data.cache.PhotoDatabase
import com.android.pexels.data.cache.Photo

class PhotoListViewModel(application: Application) : AndroidViewModel(application) {

    var repository: PhotoRepository

    init {
        val database: PhotoDatabase =
            Room.databaseBuilder<PhotoDatabase>(
                getApplication(),
                PhotoDatabase::class.java,
                "PexelDatabase"
            ).build()
        repository = PhotoRepository(PhotoService(), database.getPhotoDAO())
    }

    val photoList = MutableLiveData<List<Photo>>()

    val photoFetchError = MutableLiveData<String>()

    fun loadPhotos(pageIndex: Int, photosCount: Int) {
        repository.loadPhotos(pageIndex, photosCount, photoFetchError).observeForever {
            photoList.postValue(it)
        }
    }
}
