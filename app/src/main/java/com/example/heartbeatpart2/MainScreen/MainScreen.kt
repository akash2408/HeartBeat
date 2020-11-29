package com.example.heartbeatpart2.MainScreen

import android.app.Activity
import android.app.Application
import android.content.ContentValues.TAG
import android.content.Context
import android.nfc.Tag
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.heartbeatpart2.Favourite.Songs
import com.example.heartbeatpart2.PlayMusic.PlayMusic
import com.example.heartbeatpart2.PlayMusic.PlayMusicViewModel
import com.example.heartbeatpart2.R
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class MainScreen : Fragment() {
  var getSongsList=ArrayList<Songs>()
    var mainScreenViewModel:MainScreenViewModel? = null
    var nowPlayingBottombar: RelativeLayout? = null
    var  playPauseButton: ImageButton? = null
    var songtitle: TextView? = null
    var visiblelayout: RelativeLayout? = null
    var noSongs: RelativeLayout? = null
    var myActivity: Activity? = null
    var recyclerView: RecyclerView? = null
    var adapter: MainScreenAdapter? = null
    var trackPosition = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_main_screen, container, false)
        setHasOptionsMenu(true)
        visiblelayout = view?.findViewById<RelativeLayout>(R.id.visible_layout)
        noSongs = view?.findViewById<RelativeLayout>(R.id.nosongs)
        nowPlayingBottombar = view?.findViewById<RelativeLayout>(R.id.Hidden_bar)
        songtitle = view?.findViewById<TextView>(R.id.song_title)
        playPauseButton = view?.findViewById<ImageButton>(R.id.play_pause)
        recyclerView = view?.findViewById<RecyclerView>(R.id.contentMain)
        mainScreenViewModel= ViewModelProviders.of(this).get(MainScreenViewModel::class.java)
        mainScreenViewModel?.SongsList?.observe(this, Observer {it->
            getSongsList=it
            if (it == null) {
                    visiblelayout?.visibility = View.INVISIBLE
                    noSongs?.visibility = View.VISIBLE
                }
            else{
                adapter= MainScreenAdapter(it)
                recyclerView?.adapter=adapter
                }
            })
        bottomBarSetup()
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.overview_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        mainScreenViewModel?.updateFilter(
            when (item.itemId) {
                R.id.action_sort_recent ->ListFilter.RECENT
                R.id.action_sort_ascending ->ListFilter.SORT
                else -> return false
            }
        )
        return true
    }
    fun bottomBarSetup(){
        try
            {bottomBarClickHandler()
            songtitle?.text=PlayMusicViewModel.Statified.songTitle.value
            PlayMusicViewModel.Statified.mediaPlayer?.setOnCompletionListener({
                songtitle?.text=PlayMusicViewModel.Statified.songTitle.value
            })
            if(PlayMusicViewModel.Statified.mediaPlayer?.isPlaying as Boolean)
            {nowPlayingBottombar?.visibility = View.VISIBLE}
            else
            {nowPlayingBottombar?.visibility = View.INVISIBLE}
        }
        catch (e:Exception)
            {e.printStackTrace()}
    }
    fun bottomBarClickHandler(){
        nowPlayingBottombar?.setOnClickListener({
            var songs=Songs(PlayMusicViewModel.Statified.songId.value as Long,PlayMusicViewModel.Statified.songTitle.value as String,PlayMusicViewModel.Statified.songArtist.value as String,
            PlayMusicViewModel.Statified.songData.value as String ,PlayMusicViewModel.Statified.date_added.value as Long)
            it.findNavController().navigate(MainScreenDirections.actionMainScreenToPlayMusic(PlayMusicViewModel.Statified.currentPosition.value as Int,PlayMusic.Statified.templist as Array<out Songs>,songs,"BottomBar"))
        })
        playPauseButton?.setOnClickListener({
            if(PlayMusicViewModel.Statified.mediaPlayer?.isPlaying as Boolean){
                PlayMusicViewModel.Statified.mediaPlayer?.pause()
                PlayMusicViewModel.Statified.isPlaying.value = false
                trackPosition = PlayMusicViewModel.Statified.mediaPlayer?.currentPosition as Int
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
            }
            else{
                PlayMusicViewModel.Statified.mediaPlayer?.seekTo(trackPosition)
                PlayMusicViewModel.Statified.mediaPlayer?.start()
                PlayMusicViewModel.Statified.isPlaying.value = true
                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        })
    }


}
