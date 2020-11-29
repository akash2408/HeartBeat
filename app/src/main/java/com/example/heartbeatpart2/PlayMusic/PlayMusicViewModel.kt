package com.example.heartbeatpart2.PlayMusic

import android.app.Application
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.widget.Toast
import androidx.lifecycle.*
import com.example.heartbeatpart2.Favourite.FavouriteDatabaseDao
import com.example.heartbeatpart2.Favourite.Songs
import kotlinx.coroutines.*
import java.lang.Runnable
import kotlin.random.Random

const val ONE_SECOND = 1000L
const val COUNTDOWN_TIME = 30000L
class PlayMusicViewModel(val database: FavouriteDatabaseDao, var app: Application) : ViewModel() {
    var MY_PREF_SHUFFLE = "shuffle feature"
    var MY_PREF_LOOP = "loop feature"
    var job = Job()
    var uiScope = CoroutineScope(Dispatchers.Main + job)
    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    var prefs =
        app.getSharedPreferences("com.example.heartbeatpart2.PlayMusic", Context.MODE_PRIVATE)

    object Statified {
        var mediaPlayer: MediaPlayer? = null
        var songTitle = MutableLiveData<String>()
        var songId = MutableLiveData<Long>()
        var songData = MutableLiveData<String>()
        var date_added = MutableLiveData<Long>()
        var songArtist = MutableLiveData<String>()
        var currentPosition = MutableLiveData<Int>()
        var isPlaying=MutableLiveData<Boolean>()
    }

    var favSize = MutableLiveData<Int>()
    var currentTime = MutableLiveData<Int>()
    var fetchSong = MutableLiveData<List<Songs>>()
    var duration = MutableLiveData<Int>()

    var isLoop = MutableLiveData<Boolean>()
    var isshuffle = MutableLiveData<Boolean>()
    var trackPosition = MutableLiveData<Int>()
    var currentFav = MutableLiveData<Boolean>()
    var favourites=database.getAllFavourites()
    init {
        Statified.mediaPlayer = MediaPlayer()
        Statified.mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
    }

    fun nextSong(songPosition: Int) {
        if (songPosition == fetchSong.value?.size?.minus(1)) {
            Statified.currentPosition.value = 0
        } else {
            Statified.currentPosition.value = Statified.currentPosition.value?.plus(1)
        }
    }

    fun onsongComplete(songPosition: Int) {
        if (loadLoop() == true) {
            Statified.currentPosition.value = songPosition
        } else if (loadShuffle() == true) {
            Statified.currentPosition.value = Random.nextInt(fetchSong.value?.size?.plus(1) as Int)
        } else {
            nextSong(songPosition)
        }
    }

    fun previousSong(songPosition: Int) {
        if (songPosition == 0) {
            Statified.currentPosition.value = 0
        } else {
            Statified.currentPosition.value = Statified.currentPosition.value?.minus(1)
        }
    }

    fun changeInformation(currentSong: Int, check: String) {
        var newSong = fetchSong?.value?.get(currentSong)
        Statified.songArtist.value = newSong?.artist
        Statified.songId.value = newSong?.song_id
        Statified.songTitle.value = newSong?.song_title
        Statified.songData.value = newSong?.song_data
        Statified.date_added.value = newSong?.date_added
        duration.value = Statified.mediaPlayer?.duration
        Statified.mediaPlayer?.timer()
        if (check != "BottomBar" && check!= "FavBottombar") {
            Statified.mediaPlayer?.reset()
            Statified.mediaPlayer?.setDataSource(app, Uri.parse(newSong?.song_data))
            Statified.mediaPlayer?.prepare()
            Statified.mediaPlayer?.start()
            duration.value = Statified.mediaPlayer?.duration
            Statified.isPlaying.value=true
        }

        checkExist()
    }

    fun MediaPlayer.timer() {
        var timer = object : Runnable {
            override fun run() {
                currentTime.value = Statified.mediaPlayer?.currentPosition
                Handler().postDelayed(this, 1000)
            }
        }
        Handler().postDelayed(timer, 1000)
    }

    fun loadLoop(): Boolean =
        prefs.getBoolean(MY_PREF_LOOP, false)

    fun loadShuffle(): Boolean =
        prefs.getBoolean(MY_PREF_SHUFFLE, false)

    fun saveLoop(loop: Boolean) =
        prefs.edit().putBoolean(MY_PREF_LOOP, loop).apply()

    fun saveShuffle(shuffle: Boolean) =
        prefs.edit().putBoolean(MY_PREF_SHUFFLE, shuffle).apply()


    fun addfav() {
        uiScope.launch {
            var cur = Statified.currentPosition.value
            addtoFavourite(cur as Int)
            Toast.makeText(app, "Added To Favourites", Toast.LENGTH_SHORT).show()
            currentFav?.value=true
        }
    }
    private suspend fun addtoFavourite(pos: Int) {
        withContext(Dispatchers.IO) {
            var newfav = fetchSong.value?.get(pos)
            database.storeAsFavourite(newfav as Songs)
        }
    }

    fun checkExist() {
        uiScope.launch {
            if (check() == 0) {
                currentFav.value = false
            } else {
                currentFav.value = true
            }
        }
    }

    private suspend fun check(): Int {
        return withContext(Dispatchers.IO) {
            database.checkifIdExists(fetchSong.value?.get(Statified.currentPosition.value as Int)?.song_id as Long)
        }
    }

    fun getSize() {
        uiScope.launch {
            Toast.makeText(app, "Size is ${favSize()}", Toast.LENGTH_SHORT).show()

        }
    }

    private suspend fun favSize(): Int {
        return withContext(Dispatchers.IO) {
               database.listSize()
        }

    }

    fun favDelete() {
        uiScope.launch {
            delete()
            Toast.makeText(app, "Remove from Favourites", Toast.LENGTH_SHORT).show()
            currentFav.value=false
        }
    }

    private suspend fun delete() {
        withContext(Dispatchers.IO) {
            database.deleteFavourtie(fetchSong.value?.get(Statified.currentPosition.value as Int)?.song_id as Long)
        }
    }
}

class PlayMusicViewModelFactory(
    private val dataSource: FavouriteDatabaseDao,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayMusicViewModel::class.java)) {
            return PlayMusicViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}