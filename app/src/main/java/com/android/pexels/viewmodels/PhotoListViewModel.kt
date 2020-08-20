package com.android.pexels.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.android.pexels.api.PexelPhotoService
import com.android.pexels.data.PhotoRepository
import com.android.pexels.data.cache.PhotoDatabase
import com.android.pexels.data.cache.PhotoEntity

class PhotoListViewModel(application: Application) : AndroidViewModel(application) {

    lateinit var repository: PhotoRepository

    init {
        val database: PhotoDatabase =
            Room.databaseBuilder<PhotoDatabase>(
                getApplication(),
                PhotoDatabase::class.java,
                "PexelDatabase"
            ).build()
        repository = PhotoRepository(PexelPhotoService(), database.getPhotoDAO())
    }

    val photoList = MutableLiveData<List<PhotoEntity>>();

    fun loadPhotos(page: Int, count: Int) {
        repository.loadPhotos(page, count).observeForever {
            photoList.postValue(it)
        }

    }


}
