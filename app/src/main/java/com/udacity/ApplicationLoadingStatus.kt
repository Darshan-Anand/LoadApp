package com.udacity

import android.app.Application
import com.udacity.utils.createNotificationChannel
import timber.log.Timber

class ApplicationLoadingStatus : Application(){
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        createNotificationChannel(this)
    }
}