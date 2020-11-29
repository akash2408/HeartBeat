package com.example.heartbeatpart2.PlayMusic

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.*
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import com.example.heartbeatpart2.Favourite.FavouriteDatabase
import com.example.heartbeatpart2.Favourite.Songs
import com.example.heartbeatpart2.MainScreen.MainScreenViewModel

import com.example.heartbeatpart2.R
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue

/**
 * A simple [Fragment] subclass.
 */
class PlayMusic : Fragment() {
    object Statified{
        var templist:Array<out Songs>?=null}
    var viewmodel: PlayMusicViewModel? = null
    var myActivity: Activity? = null
    var startTimeText: TextView? = null
    var endTimeText: TextView? = null
    var playpauseImageButton: ImageButton? = null
    var previousImageButton: ImageButton? = null
    var nextImageButton: ImageButton? = null
    var loopImageButton: ImageButton? = null
    var seekBar: SeekBar? = null
    var songArtistsView: TextView? = null
    var songTitleView: TextView? = null
    var shuffleImageButton: ImageButton? = null
    var currentPosition: Int = 0
    var fetchSongs: ArrayList<Songs>? = null
    var fab: ImageButton? = null
    var audiovisualization: AudioVisualization? = null
    var glview: GLAudioVisualizationView? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_play_music, container, false)
        seekBar = view?.findViewById(R.id.sekBar)
        startTimeText = view?.findViewById(R.id.StartTime)
        endTimeText = view?.findViewById(R.id.EndTime)
        playpauseImageButton = view?.findViewById(R.id.playpausebutton)
        nextImageButton = view?.findViewById(R.id.nextButton)
        previousImageButton = view?.findViewById(R.id.previousButton)
        loopImageButton = view?.findViewById(R.id.loopButton)
        shuffleImageButton = view?.findViewById(R.id.shuffleButton)
        songArtistsView = view?.findViewById(R.id.songArtist)
        songTitleView = view?.findViewById(R.id.songTitle)
        fab = view?.findViewById(R.id.favouriteIcon)
        glview = view?.findViewById(R.id.visualizer_view)
        fab?.alpha = 0.8f
        val application = requireNotNull(this.activity).application
        val datasource= FavouriteDatabase.getInstance(application).favouriteDatabaseDao
        val viewModelFactory = PlayMusicViewModelFactory(datasource,application)
        viewmodel = ViewModelProvider(requireActivity(),viewModelFactory).get(PlayMusicViewModel::class.java)
        var args = PlayMusicArgs.fromBundle(arguments!!)
        PlayMusicViewModel.Statified.currentPosition.value = args.songPostion
        var navigatefrom=args.navigateFrom
        viewmodel?.fetchSong?.value = args.songList.SongList()
        Statified.templist=args.songList
        PlayMusicViewModel.Statified.currentPosition.observe(viewLifecycleOwner, Observer { temp ->
            currentPosition = temp
            viewmodel?.changeInformation(currentPosition,navigatefrom)
            if(navigatefrom=="BottomBar")
            {navigatefrom="MainScreenAdapter"}
            else
            {navigatefrom="favReclerview"}
            if (viewmodel?.loadLoop() == true) {
                loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
            } else {
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
            }
            if (viewmodel?.loadShuffle() == true) {
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
            } else {
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            }
        })
        viewmodel?.currentFav?.observe(viewLifecycleOwner, Observer {it->
            if(it==false)
                {fab?.setImageResource(R.drawable.favorite_off)}
            else
            {fab?.setImageResource(R.drawable.favorite_on)}
        })
        fab?.setOnClickListener({
            if(viewmodel?.currentFav?.value==false)
            { viewmodel?.addfav()}
            else
            {viewmodel?.favDelete()
            }
        })
        viewmodel?.currentTime?.observe(this, Observer { timer ->
                    if(TimeUnit.MILLISECONDS.toSeconds(PlayMusicViewModel.Statified.mediaPlayer?.currentPosition?.toLong() as Long) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(PlayMusicViewModel.Statified.mediaPlayer?.currentPosition?.toLong() as Long))<10)
                    {startTimeText?.setText(
                        String.format(
                            "%d:0%d",TimeUnit.MILLISECONDS.toMinutes(PlayMusicViewModel.Statified.mediaPlayer?.currentPosition?.toLong() as Long),TimeUnit.MILLISECONDS.toSeconds(PlayMusicViewModel.Statified.mediaPlayer?.currentPosition?.toLong() as Long) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(PlayMusicViewModel.Statified.mediaPlayer?.currentPosition?.toLong() as Long))))
                    }
                    else
                    {startTimeText?.setText(
                        String.format(
                            "%d:%d",TimeUnit.MILLISECONDS.toMinutes(PlayMusicViewModel.Statified.mediaPlayer?.currentPosition?.toLong() as Long),TimeUnit.MILLISECONDS.toSeconds(PlayMusicViewModel.Statified.mediaPlayer?.currentPosition?.toLong() as Long) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(PlayMusicViewModel.Statified.mediaPlayer?.currentPosition?.toLong() as Long))))
                    }
            PlayMusicViewModel.Statified.mediaPlayer?.setOnCompletionListener {
                viewmodel?.onsongComplete(currentPosition)
            }
            seekBar?.setProgress(timer)
        })
        seekBar?.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser){
                    PlayMusicViewModel.Statified?.mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                PlayMusicViewModel.Statified.cur.value = PlayMusicViewModel.Statified.mediaPlayer?.currentPosition
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                PlayMusicViewModel.Statified.currentPosition.value = PlayMusicViewModel.Statified.mediaPlayer?.currentPosition

            }

        })

        loopImageButton?.setOnClickListener({
            if (viewmodel?.loadLoop() == false) {
                viewmodel?.saveLoop(true)
                viewmodel?.saveShuffle(false)
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
            } else {
                viewmodel?.saveLoop(false)
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
            }
        })
        shuffleImageButton?.setOnClickListener({
            if (viewmodel?.loadShuffle() == false) {
                viewmodel?.saveShuffle(true)
                viewmodel?.saveLoop(false)
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
            } else {
                viewmodel?.saveShuffle(false)
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            }

        })
        nextImageButton?.setOnClickListener({

            viewmodel?.nextSong(currentPosition)
            playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        })
        previousImageButton?.setOnClickListener({
            viewmodel?.previousSong(currentPosition)
            playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        })
        viewmodel?.duration?.observe(this, Observer { timer ->
            startTimeText?.setText(
                String.format(
                    "%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(PlayMusicViewModel.Statified.mediaPlayer?.currentPosition?.toLong() as Long),
                    TimeUnit.MILLISECONDS.toSeconds(PlayMusicViewModel.Statified.mediaPlayer?.currentPosition?.toLong() as Long) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(PlayMusicViewModel.Statified.mediaPlayer?.currentPosition?.toLong() as Long)
                )
                ))
            endTimeText?.setText(
                String.format(
                    "%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(timer.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(timer.toLong()) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timer.toLong()))
                )
            )
            seekBar?.max = timer
            seekBar?.setProgress(PlayMusicViewModel.Statified.mediaPlayer?.currentPosition as Int)

        })
        updateTextView()
        PlayMusicViewModel.Statified.isPlaying.observe(this, Observer { it->
            if(it==false){
                playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
            }
            else
            {playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)}
        })
        playpauseImageButton?.setOnClickListener({
            if (PlayMusicViewModel.Statified.isPlaying.value == true) {
                PlayMusicViewModel.Statified.isPlaying.value = false
                PlayMusicViewModel.Statified.mediaPlayer?.pause()

            } else {
                PlayMusicViewModel.Statified.isPlaying.value = true
                PlayMusicViewModel.Statified.mediaPlayer?.start()

            }
        })
        return view
    }

    fun updateTextView() {
        PlayMusicViewModel.Statified.songArtist?.observe(this, Observer { artist ->
            var songArtistUpdated = artist
            if (artist.equals("<unknown>", true)) {
                songArtistUpdated = "unknown"
            }
            songArtistsView?.setText(songArtistUpdated)

        })
        PlayMusicViewModel.Statified.songTitle?.observe(this, Observer { title ->
            var songTitleUpdated = title
            if (title.equals("<unknown>", true)) {
                songTitleUpdated = "unknown"
            }
            songTitleView?.setText(songTitleUpdated)
        })
    }

    fun Array<Songs>.SongList(): List<Songs> {
        return map {
            Songs(
                artist = it.artist,
                song_title = it.song_title,
                date_added = it.date_added,
                song_data = it.song_data,
                song_id = it.song_id
            )
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        audiovisualization = glview as AudioVisualization
    }

    override fun onResume() {
       audiovisualization?.onResume()
        super.onResume()
    }

    override fun onPause() {
        audiovisualization?.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        audiovisualization?.release()
        super.onDestroy()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var visualizationHandler =
            DbmHandler.Factory.newVisualizerHandler(context as Context, 0)
        audiovisualization?.linkTo(visualizationHandler)
    }

}
