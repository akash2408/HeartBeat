package com.example.heartbeatpart2.Favourite

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.heartbeatpart2.MainScreen.refreshlist
import com.example.heartbeatpart2.PlayMusic.PlayMusic
import com.example.heartbeatpart2.PlayMusic.PlayMusicViewModel
import com.example.heartbeatpart2.R
import kotlinx.android.synthetic.main.activity_main.*


class FavouriteAdapter (var favDetails:List<Songs>):ListAdapter<Songs,FavouriteAdapter.ViewHolder>(refreshlist()) {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var trackTitle = itemView.findViewById<TextView>(R.id.track_title)
        var trackArtist = itemView.findViewById<TextView>(R.id.track_artist)
        var contentHolder = itemView.findViewById<RelativeLayout>(R.id.contentRow)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context)
            .inflate(R.layout.raw_custom_navigation_song, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return favDetails.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var data = favDetails.get(position)
        holder.trackTitle.text = favDetails.get(position).song_title
        holder.trackArtist.text = favDetails.get(position).artist
        holder.contentHolder.setOnClickListener({
            PlayMusicViewModel.Statified.mediaPlayer?.reset()
            it.findNavController().navigate(
                FavouriteDirections.actionFavouriteToPlayMusic(
                    position,
                    favDetails?.toTypedArray(),
                    data,
                    "favReclerview"
                )
            )
        })

    }

    class refreshlist : DiffUtil.ItemCallback<Songs>() {
        override fun areItemsTheSame(oldItem: Songs, newItem: Songs): Boolean {
            return oldItem.song_id == newItem.song_id
        }

        override fun areContentsTheSame(oldItem: Songs, newItem: Songs): Boolean {
            return oldItem == newItem
        }
    }
}
