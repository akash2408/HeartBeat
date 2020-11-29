package com.example.heartbeatpart2.Favourite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.heartbeatpart2.MainScreen.MainScreen
import com.example.heartbeatpart2.MainScreen.MainScreenDirections
import com.example.heartbeatpart2.MainScreen.MainScreenViewModel
import com.example.heartbeatpart2.PlayMusic.PlayMusic
import com.example.heartbeatpart2.PlayMusic.PlayMusicViewModel
import com.example.heartbeatpart2.PlayMusic.PlayMusicViewModelFactory

import com.example.heartbeatpart2.R


/**
 * A simple [Fragment] subclass.
 */
class Favourite : Fragment() {
    var adapter: FavouriteAdapter? = null
    var recyclerView: RecyclerView? = null
    var viewmodel: PlayMusicViewModel? = null
    var songtitle: TextView? = null
    var nowPlayingBottombar: RelativeLayout? = null
    var noFavourite: TextView? = null
    var  playPauseButton: ImageButton? = null
    var trackPosition = 0
    var favSongList:List<Songs>?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)
        noFavourite = view?.findViewById(R.id.noFavourite)
        nowPlayingBottombar = view?.findViewById(R.id.fav_Hidden_bar)
        recyclerView = view?.findViewById(R.id.fav_contentMain)
        playPauseButton = view?.findViewById<ImageButton>(R.id.fav_play_pause)
        songtitle = view?.findViewById<TextView>(R.id.fav_song_title)
        val application = requireNotNull(this.activity).application
        val datasource = FavouriteDatabase.getInstance(application).favouriteDatabaseDao
        val viewModelFactory = PlayMusicViewModelFactory(datasource, application)
        viewmodel =
            ViewModelProvider(
                requireActivity(),
                viewModelFactory
            ).get(PlayMusicViewModel::class.java)

            viewmodel?.favourites?.observe(this, Observer {
            favSongList=it
            if(it.size!=0)
                {   adapter= FavouriteAdapter(it)
                    recyclerView?.adapter=adapter
                }
            else
                {   recyclerView?.visibility=View.INVISIBLE
                    noFavourite?.visibility=View.VISIBLE

                }
            })
        bottomBarSetup()
        return view
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
            var songs= Songs(PlayMusicViewModel.Statified.songId.value as Long,PlayMusicViewModel.Statified.songTitle.value as String,PlayMusicViewModel.Statified.songArtist.value as String,
                PlayMusicViewModel.Statified.songData.value as String ,PlayMusicViewModel.Statified.date_added.value as Long)
            it.findNavController().navigate(FavouriteDirections.actionFavouriteToPlayMusic(PlayMusicViewModel.Statified.currentPosition.value as Int,
                PlayMusic.Statified.templist as Array<out Songs>,songs,"FavBottombar"))
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
