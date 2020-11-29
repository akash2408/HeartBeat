package com.example.heartbeatpart2.Favourite

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations.map
import androidx.room.*
import kotlinx.android.parcel.Parcelize
@Parcelize
@Entity(tableName = "favourite_song")
data class Songs(
    @PrimaryKey
    var song_id:Long,
    @ColumnInfo(name = "title")
    var song_title: String,
    @ColumnInfo(name = "artist")
    var artist: String,
    @ColumnInfo(name = "path")
    var song_data:String,
    @ColumnInfo(name ="date" )
    var date_added:Long): Parcelable {
    object Statified{
        var nameComparator:Comparator<Songs> = Comparator<Songs>{song1, song2 ->
            val songOne= song1.song_title.toUpperCase()
            val songTwo = song2.song_title.toUpperCase()
            songOne.compareTo(songTwo)
        }
        var dateComparator:Comparator<Songs> = Comparator<Songs>{ song1, song2 ->
            val songOne = song1.date_added.toDouble()
            val songTwo = song2.date_added.toDouble()
            songTwo.compareTo(songOne)
        }
    }
}
@Dao
interface FavouriteDatabaseDao {
    @Insert
    fun storeAsFavourite(song:Songs)

    @Query("SELECT * FROM favourite_song")
    fun getAllFavourites():LiveData<List<Songs>>

    @Query("SELECT COUNT(song_id) FROM favourite_song WHERE song_id=:key")
    fun checkifIdExists(key:Long):Int

    @Query("DELETE FROM favourite_song WHERE song_id=:key")
    fun deleteFavourtie(key:Long)

    @Query("SELECT COUNT(*) FROM favourite_song")
    fun listSize():Int
}

@Database(entities = [Songs::class],version = 1,exportSchema = false)
abstract class FavouriteDatabase :RoomDatabase(){
    abstract val favouriteDatabaseDao:FavouriteDatabaseDao
    companion object{
        @Volatile
        private var INSTANCE:FavouriteDatabase? = null

        fun getInstance(context: Context):FavouriteDatabase{
            synchronized(this){
                var instance = INSTANCE
                if(instance==null) {
                    instance= Room.databaseBuilder(context.applicationContext,FavouriteDatabase::class.java,
                        "favourite_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE=instance
                }
                return instance
            }
        }
    }

}
