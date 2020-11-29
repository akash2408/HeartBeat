package com.example.heartbeatpart2.MainScreen

import android.app.Application
import android.content.Context
import android.media.MediaActionSound
import android.media.MediaPlayer
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.heartbeatpart2.Favourite.Songs
import com.example.heartbeatpart2.PlayMusic.PlayMusicViewModel
import java.util.*
import kotlin.collections.ArrayList
enum class ListFilter(val value: String) { RECENT("recent"),SORT("sort")}
class MainScreenViewModel (var app: Application) : AndroidViewModel(app) {
    var RECENT_PREF = "action_sort_recent"
    var SORT_PREF = "action_sort_ascending"
    var prefs =
        app.getSharedPreferences("com.example.heartbeatpart2.MainScreen", Context.MODE_PRIVATE)

    var SongsList = MutableLiveData<ArrayList<Songs>>()

    init {
        if(loadRecent()==true)
            {getSongsFromPhone(ListFilter.RECENT)}
        else
            {getSongsFromPhone(ListFilter.SORT)}
    }

    fun getSongsFromPhone(filter:ListFilter){
        var arrayList = ArrayList<Songs>()
        var contentResolver = app.contentResolver
        var songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var songCursor = contentResolver?.query(songUri, null, null, null, null)
        if (songCursor != null && songCursor.moveToFirst()) {
            val songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val songDate = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
            while (songCursor.moveToNext()) {
                var curid = songCursor.getLong(songId)
                var curtitle = songCursor.getString(songTitle)
                var curArtist = songCursor.getString(songArtist)
                var curdata = songCursor.getString(songData)
                var curdate = songCursor.getLong(songDate)
                arrayList.add(Songs(curid, curtitle, curArtist, curdata, curdate))
            }
        }
        if(filter==ListFilter.RECENT)
                { Collections.sort(arrayList, Songs.Statified.dateComparator)
                 SongsList.value=arrayList
                }
        else
            {   Collections.sort(arrayList, Songs.Statified.nameComparator)
                SongsList.value=arrayList}
    }

    fun loadRecent(): Boolean =
        prefs.getBoolean(RECENT_PREF, true)

    fun saveRecent(recent: Boolean) =
        prefs.edit().putBoolean(RECENT_PREF, recent).apply()

    fun loadSort(): Boolean =
        prefs.getBoolean(SORT_PREF, false)

    fun saveSort(recent: Boolean) =
        prefs.edit().putBoolean(SORT_PREF, recent).apply()

    fun updateFilter(filter: ListFilter) {
        if(filter==ListFilter.SORT)
            {   saveSort(true)
                saveRecent(false)
                getSongsFromPhone(filter)}
        else
            {   saveSort(false)
                saveRecent(true)
                getSongsFromPhone(filter)}
    }

}
