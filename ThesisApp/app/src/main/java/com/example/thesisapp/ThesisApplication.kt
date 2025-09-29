package com.example.thesisapp

import android.app.Application
import com.example.thesisapp.data.AppContainer
import com.example.thesisapp.data.AppDataContainer

class ThesisApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}