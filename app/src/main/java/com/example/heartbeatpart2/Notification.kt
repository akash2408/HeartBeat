package com.example.heartbeatpart2

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat


private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAG=0
val ACTION_PREVIUOS = "actionprevious"
val ACTION_PLAY = "actionplay"
val ACTION_NEXT = "actionnext"
        fun NotificationManager.sendNotification(
            applicationContext: Context,
            artist: String,
            title: String,
            play: Boolean
        ) {
            val mediaSessionCompat = MediaSessionCompat(applicationContext, "tag")
            val intent = Intent(applicationContext, MainActivity::class.java)
            var pause: Int? = null
            val contentPendingIntent =
                PendingIntent.getActivity(
                    applicationContext,
                    NOTIFICATION_ID,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            val songImage = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.equlizer)
            val prev = 0
            val previntent = Intent(applicationContext, NotificationService::class.java)?.setAction(ACTION_PREVIUOS )
            if (play == false) {
                pause = R.drawable.play_icon
            } else {
                pause = R.drawable.pause_icon
            }
            val prevPendingIntent =
                PendingIntent.getBroadcast(applicationContext, REQUEST_CODE, previntent,PendingIntent.FLAG_UPDATE_CURRENT)
            val pauseintent =
                Intent(applicationContext, NotificationService::class.java)?.setAction(ACTION_PLAY )
            val pausePendingIntent =
                PendingIntent.getBroadcast(
                    applicationContext,
                    REQUEST_CODE,
                    pauseintent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            val nextintent = Intent(applicationContext, NotificationService::class.java)?.setAction(
                ACTION_NEXT)
            val nextPendingIntent =
                PendingIntent.getBroadcast(
                    applicationContext,
                    REQUEST_CODE,
                    nextintent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

            val builder = NotificationCompat.Builder(applicationContext, "songchannel")
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.equlizer)
                .setLargeIcon(songImage)
                .setContentTitle(title)
                .setContentText(artist)
                .setStyle(
                    androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2))
                .addAction(R.drawable.play_previous_icon, "Previous", prevPendingIntent) // #0
                .addAction(pause, "Pause", pausePendingIntent) // #1
                .addAction(R.drawable.baseline_skip_next_black_48dp, "Next", nextPendingIntent)
                .setContentIntent(contentPendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()
            notify(NOTIFICATION_ID, builder)

        }