package com.example.miskiparqueo.di

import com.example.miskiparqueo.feature.reservation.data.datasource.ParkingExtrasDataSource
import com.example.miskiparqueo.feature.reservation.data.repository.ReservationRepositoryImpl
import com.example.miskiparqueo.feature.reservation.domain.repository.IReservationRepository
import com.example.miskiparqueo.feature.reservation.domain.usecases.GetReservationDetailUseCase
import com.example.miskiparqueo.feature.reservation.presentation.ReservationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val reservationModule = module {
    single { ParkingExtrasDataSource() }
    single<IReservationRepository> { ReservationRepositoryImpl(get(), get()) }
    single { GetReservationDetailUseCase(get()) }

    viewModel { (parkingId: String) ->
        ReservationViewModel(
            parkingId = parkingId,
            getReservationDetailUseCase = get()
        )
    }
}
