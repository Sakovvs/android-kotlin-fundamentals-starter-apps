package com.example.android.dessertclicker

import android.app.Application
import timber.log.Timber

class ClickerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //нициализируйте Timber библиотеку
        Timber.plant(Timber.DebugTree())
    }
}