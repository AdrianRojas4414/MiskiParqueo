package com.miskidev.miskiparqueo

import android.app.Application
import com.miskidev.miskiparqueo.di.authModule
import com.miskidev.miskiparqueo.di.maintenanceModule
import com.miskidev.miskiparqueo.di.mapModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import com.miskidev.miskiparqueo.di.profileModule
import com.miskidev.miskiparqueo.di.reservationModule

class App: Application() {
    override fun onCreate(){
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(authModule, mapModule, profileModule, reservationModule, maintenanceModule)
        }
    }
}
