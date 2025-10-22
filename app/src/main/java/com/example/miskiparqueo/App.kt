package com.example.miskiparqueo

import android.app.Application
import com.example.miskiparqueo.di.authModule
import com.example.miskiparqueo.di.profileModule // <-- 1. Agrega este import
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App: Application() {
    override fun onCreate(){
        super.onCreate()
        startKoin {
            androidContext(this@App)
            // 2. Añade el nuevo módulo aquí, separado por una coma
            modules(authModule, profileModule)
        }
    }
}