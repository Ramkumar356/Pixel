package com.android.pexels.data.cache

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.IGNORE

@Database(entities = [Photo::class], version = 1, exportSchema = false)
abstract class PhotoDatabase : RoomDatabase() {

    abstract fun getPhotoDAO(): PhotoDAO

    companion object {
        var INSTANCE: PhotoDatabase? = null
        fun getInstance(context: Application): PhotoDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder<PhotoDatabase>(
                    context,
                    PhotoDatabase::class.java,
                    "PexelDatabase"
                ).build();
            }
            return INSTANCE as PhotoDatabase
        }
    }
}

@Entity(primaryKeys = ["id"])
data class Photo(
    var id: Int,
    var width: Int,
    var height: Int,
    var url: String,
    val original: String?,
    val large2x: String?,
    val large: String?,
    val medium: String?,
    val small: String?,
    val tiny: String?,
    val portrait: String?
)

@Dao
interface PhotoDAO {

    @Query("SELECT * FROM Photo LIMIT :count OFFSET :offset")
    fun getPhotos(count: Int, offset: Int): LiveData<List<Photo>>

    @Insert(onConflict = IGNORE)
    fun insertPhotos(photos: List<Photo>)
}