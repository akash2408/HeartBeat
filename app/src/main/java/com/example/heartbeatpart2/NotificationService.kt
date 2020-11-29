package com.example.heartbeatpart2

import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class NotificationService :BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.sendBroadcast(Intent("TRACKS").putExtra("actionname",intent?.action))
    }

}