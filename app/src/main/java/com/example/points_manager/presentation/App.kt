package com.example.points_manager.presentation

import android.app.Application
import com.example.points_manager.di.AppComponent
import com.example.points_manager.di.DaggerAppComponent

class App: Application() {

    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        appComponent = DaggerAppComponent.builder().context(context = this).build()
        super.onCreate()
    }
}