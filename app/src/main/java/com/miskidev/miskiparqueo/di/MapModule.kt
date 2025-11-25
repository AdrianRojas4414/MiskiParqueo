package com.miskidev.miskiparqueo.di

import com.miskidev.miskiparqueo.feature.map.data.datasource.ParkingLocationDataSource
import com.miskidev.miskiparqueo.feature.map.data.repository.LocationRepositoryImpl
import com.miskidev.miskiparqueo.feature.map.data.repository.ParkingRepositoryImpl
import com.miskidev.miskiparqueo.feature.map.domain.repository.ILocationRepository
import com.miskidev.miskiparqueo.feature.map.domain.repository.IParkingRepository
import com.miskidev.miskiparqueo.feature.map.domain.usecases.CalculateRouteUseCase
import com.miskidev.miskiparqueo.feature.map.domain.usecases.GetParkingLocationsUseCase
import com.miskidev.miskiparqueo.feature.map.domain.usecases.SearchParkingsUseCase
import com.miskidev.miskiparqueo.feature.map.presentation.MapViewModel
import org.koin.android.ext.koin.androidContext
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
    single { SearchParkingsUseCase(get()) }
    single { CalculateRouteUseCase() }
    //================================================
    // MAP - CURRENT LOCATION (GPS)
    //================================================

    // Repositorio de Ubicación (necesita el Context)
    single<ILocationRepository> { LocationRepositoryImpl(androidContext()) }

    //================================================
    // MAP - VIEWMODEL
    //================================================
    viewModel { MapViewModel(getParkingLocationsUseCase = get(), get(), get()) }
}