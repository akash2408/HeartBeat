package com.example.heartbeatpart2.MainScreen

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.heartbeatpart2.Favourite.Songs
import com.example.heartbeatpart2.PlayMusic.PlayMusic
import com.example.heartbeatpart2.PlayMusic.PlayMusicArgs
import com.example.heartbeatpart2.PlayMusic.PlayMusicViewModel
import com.example.heartbeatpart2.R

class MainScreenAdapter(var songdetails: ArrayList<Songs>):androidx.recyclerview.widget.ListAdapter<Songs,MainScreenAdapter.MyViewHolder>(refreshlist()) {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var trackTitle: TextView? = null
        var trackArtist: TextView? = null
        var contentHolder: RelativeLayout? = null

        init {
            trackTitle = itemView.findViewById<TextView>(R.id.track_title)
            trackArtist = itemView.findViewById<TextView>(R.id.track_artist)
            contentHolder = itemView.findViewById<RelativeLayout>(R.id.contentRow)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.raw_custom_navigation_song, parent, false)
        return MyViewHolder(view)

    }
    override fun getItemCount(): Int {
        if (songdetails == null)
            return 0
        else
            return (songdetails as ArrayList<Songs>).size

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = songdetails.get(position)
        holder.trackTitle?.text = data.song_title
        holder.trackArtist?.text = data.artist
        holder.contentHolder?.setOnClickListener({
            PlayMusicViewModel.Statified.mediaPlayer?.reset()
            it.findNavController().navigate(MainScreenDirections.actionMainScreenToPlayMusic(position,songdetails.toTypedArray(),data,"MainScreenAdapter"))
        })
    }
}
class refreshlist:DiffUtil.ItemCallback<Songs>(){
    override fun areItemsTheSame(oldItem: Songs, newItem: Songs): Boolean {
        return oldItem.song_id==newItem.song_id
    }

    override fun areContentsTheSame(oldItem: Songs, newItem: Songs): Boolean {
        return oldItem==newItem
    }
}