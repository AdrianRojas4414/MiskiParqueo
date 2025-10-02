package com.example.miskiparqueo

import android.app.Application
import com.example.miskiparqueo.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext

class App: Application() {
    override fun onCreate(){
        super.onCreate()
        GlobalContext.startKoin {
            androidContext(this@App)
            modules(appModule)
        }
    }
}