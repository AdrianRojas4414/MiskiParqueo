package com.example.miskiparqueo

import android.app.Application
import com.example.miskiparqueo.di.authModule
import com.example.miskiparqueo.di.mapModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App: Application() {
    override fun onCreate(){
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(authModule, mapModule)
        }
    }
}