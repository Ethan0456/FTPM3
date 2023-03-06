package com.example.ftpm3

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class CustomLifeCycleObserver(val ftpViewModel: FtpViewModel): DefaultLifecycleObserver {
    private var wasPaused: Boolean = false
    override fun onResume(owner: LifecycleOwner) {
        if (!ftpViewModel.getClientInstance().isConnected and wasPaused) {
            Log.i(TAG,"Lifecycle Aware: Reconnect")
            wasPaused = false
            ftpViewModel.connect()
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        if (ftpViewModel.getClientInstance().isConnected) {
            Log.i(TAG,"Lifecycle Aware: Disconnect")
            wasPaused = true
            ftpViewModel.disconnect()
        }
    }
}