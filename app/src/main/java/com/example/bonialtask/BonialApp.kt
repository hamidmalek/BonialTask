package com.example.bonialtask

import android.app.Application
import com.example.bonialtask.di.initKoin

class BonialApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}
