package com.example.miskiparqueo.di

import com.example.miskiparqueo.feature.map.data.datasource.ParkingLocationDataSource
import com.example.miskiparqueo.feature.map.data.repository.ParkingRepositoryImpl
import com.example.miskiparqueo.feature.map.domain.repository.IParkingRepository
import com.example.miskiparqueo.feature.map.domain.usecase.GetParkingLocationsUseCase
import com.example.miskiparqueo.feature.map.presentation.MapViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mapModule = module {

    //================================================
    // MAP FEATURE DEPENDENCIES
    //================================================

    // DataSource
    single { ParkingLocationDataSource() }
    // Repository
    single<IParkingRepository> { ParkingRepositoryImpl(get()) }
    // UseCase
    single { GetParkingLocationsUseCase(get()) }
    // ViewModel
    viewModel { MapViewModel(get()) }
}