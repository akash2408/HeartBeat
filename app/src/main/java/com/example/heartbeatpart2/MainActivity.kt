package com.example.heartbeatpart2

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.heartbeatpart2.Favourite.FavouriteDatabase
import com.example.heartbeatpart2.PlayMusic.PlayMusicViewModel
import com.example.heartbeatpart2.PlayMusic.PlayMusicViewModelFactory
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    var drawerLayout:DrawerLayout?=null
    var viewmodel:PlayMusicViewModel?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawerLayout=this.findViewById(R.id.drawer_layout)
        var toolbar=this.findViewById<NavigationView>(R.id.nav_view)
        toolbar.itemIconTintList=null
        val navController = this.findNavController(R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this,navController,drawerLayout)
        NavigationUI.setupWithNavController(toolbar,navController)
        val application = requireNotNull(this).application
        val datasource= FavouriteDatabase.getInstance(application).favouriteDatabaseDao
        val viewModelFactory = PlayMusicViewModelFactory(datasource,application)
        viewmodel = ViewModelProvider(this,viewModelFactory).get(PlayMusicViewModel::class.java)
        createChannel(
            "songchannel",
            "HeartBeat Song"
        )

        var broadcastReceiver=object:BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                val action = intent?.extras?.getString("actionname")
                when(action){
                    "actionprevious"->{
                        viewmodel?.previousSong(PlayMusicViewModel.Statified.currentPosition.value as Int)
                        viewmodel?.changeInformation(PlayMusicViewModel.Statified.currentPosition.value as Int,"notify")
                        val notificationManager = ContextCompat.getSystemService(this@MainActivity, NotificationManager::class.java) as NotificationManager
                        notificationManager.sendNotification(this@MainActivity,PlayMusicViewModel.Statified.songArtist.value as String,PlayMusicViewModel.Statified.songTitle.value as String,PlayMusicViewModel.Statified.isPlaying.value as Boolean)}
                    "actionplay"->{if(PlayMusicViewModel.Statified.isPlaying.value == true)
                                        {PlayMusicViewModel.Statified.isPlaying.value =false
                                        PlayMusicViewModel.Statified.mediaPlayer?.pause()
                                            val notificationManager = ContextCompat.getSystemService(this@MainActivity, NotificationManager::class.java) as NotificationManager
                                            notificationManager.sendNotification(this@MainActivity,PlayMusicViewModel.Statified.songArtist.value as String,PlayMusicViewModel.Statified.songTitle.value as String,PlayMusicViewModel.Statified.isPlaying.value as Boolean)}

                                    else
                                        {   PlayMusicViewModel.Statified.isPlaying.value=true
                                            PlayMusicViewModel.Statified.mediaPlayer?.start()
                                            val notificationManager = ContextCompat.getSystemService(this@MainActivity, NotificationManager::class.java) as NotificationManager
                                            notificationManager.sendNotification(this@MainActivity,PlayMusicViewModel.Statified.songArtist.value as String,PlayMusicViewModel.Statified.songTitle.value as String,PlayMusicViewModel.Statified.isPlaying.value as Boolean)
                                        }

                        }
                    "actionnext"->{
                        viewmodel?.nextSong(PlayMusicViewModel.Statified.currentPosition.value as Int)
                        viewmodel?.changeInformation(PlayMusicViewModel.Statified.currentPosition.value as Int,"notify")
                        val notificationManager = ContextCompat.getSystemService(this@MainActivity, NotificationManager::class.java) as NotificationManager
                        notificationManager.sendNotification(this@MainActivity,PlayMusicViewModel.Statified.songArtist.value as String,PlayMusicViewModel.Statified.songTitle.value as String,PlayMusicViewModel.Statified.isPlaying.value as Boolean)
                    }
                }
            }
        }
        registerReceiver(broadcastReceiver, IntentFilter("TRACKS"));
    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.nav_host_fragment)
        return NavigationUI.navigateUp(navController,drawerLayout)
    }
    private fun createChannel(channelId: String, channelName: String) {
        // TODO: Step 1.6 START create a channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                // TODO: Step 2.4 change importance
                NotificationManager.IMPORTANCE_LOW
            )
                // TODO: Step 2.6 disable badges for this channel
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            // TODO: Step 1.6 END create a channel
            val notificationManager = this.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

    override fun onStop() {
        super.onStop()
        val notificationManager = ContextCompat.getSystemService(
           this,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.sendNotification(this,PlayMusicViewModel.Statified.songArtist.value as String,PlayMusicViewModel.Statified.songTitle.value as String,PlayMusicViewModel.Statified.isPlaying.value as Boolean)

    }

    override fun onResume() {
        super.onResume()
        var NotificationManager = ContextCompat.getSystemService(this,NotificationManager::class.java) as NotificationManager
        NotificationManager.cancelAll()

    }

    override fun onDestroy() {
        super.onDestroy()
        var NotificationManager = ContextCompat.getSystemService(this,NotificationManager::class.java) as NotificationManager
        NotificationManager.cancelAll()


    }

    override fun onStart() {
        super.onStart()
        var NotificationManager = ContextCompat.getSystemService(this,NotificationManager::class.java) as NotificationManager
        NotificationManager.cancelAll()
    }
}
