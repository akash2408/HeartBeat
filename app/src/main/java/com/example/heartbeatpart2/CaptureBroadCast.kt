package com.example.heartbeatpart2

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.example.heartbeatpart2.PlayMusic.PlayMusicViewModel


class CaptureBroadCast :BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.action==Intent.ACTION_NEW_OUTGOING_CALL)
            {   /*try{
                MainActivity.Statified.notificationManager?.cancel(1024)
            }
            catch (e: java.lang.Exception)
            {e.printStackTrace()}*/
                try {
                if (PlayMusicViewModel.Statified.mediaPlayer?.isPlaying as Boolean) {
                    PlayMusicViewModel.Statified.mediaPlayer?.pause()
                    PlayMusicViewModel.Statified.isPlaying.value = false
                }
            }catch (e:Exception)
            {e.printStackTrace()}
            }
        else{
            val tm:TelephonyManager = context?.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
            when(tm?.callState){
                TelephonyManager.CALL_STATE_RINGING ->{
                    /*try{
                        MainActivity.Statified.notificationManager?.cancel(1024)
                    }
                    catch (e: java.lang.Exception)
                    {e.printStackTrace()}*/
                    try {
                        if (PlayMusicViewModel.Statified.mediaPlayer?.isPlaying as Boolean) {
                            PlayMusicViewModel.Statified.mediaPlayer?.pause()
                            PlayMusicViewModel.Statified.isPlaying.value = false
                        }
                    }catch (e:Exception)
                    {e.printStackTrace()}

                }
                else-> {

                }

            }
        }

    }
}