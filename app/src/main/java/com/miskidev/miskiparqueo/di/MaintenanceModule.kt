package com.miskidev.miskiparqueo.di

import com.miskidev.miskiparqueo.feature.maintenance.data.MaintenanceDataStore
import com.miskidev.miskiparqueo.feature.maintenance.data.MaintenanceRepository
import com.miskidev.miskiparqueo.feature.maintenance.presentation.MaintenanceViewModel
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val maintenanceModule = module {
    single { MaintenanceDataStore(get()) }
    single { FirebaseRemoteConfig.getInstance() }
    single { MaintenanceRepository(get(), get()) }
    viewModel { MaintenanceViewModel(get()) }
}