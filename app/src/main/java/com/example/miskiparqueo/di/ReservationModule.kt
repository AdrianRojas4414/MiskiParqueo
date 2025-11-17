package com.example.miskiparqueo.di

import com.example.miskiparqueo.feature.reservation.data.datasource.ParkingExtrasDataSource
import com.example.miskiparqueo.feature.reservation.data.datasource.ReservationLocalDataSource
import com.example.miskiparqueo.feature.reservation.data.repository.ReservationRepositoryImpl
import com.example.miskiparqueo.feature.reservation.domain.repository.IReservationRepository
import com.example.miskiparqueo.feature.reservation.domain.usecases.ConfirmReservationUseCase
import com.example.miskiparqueo.feature.reservation.domain.usecases.GetReservationDetailUseCase
import com.example.miskiparqueo.feature.reservation.domain.usecases.ObserveActiveReservationsUseCase
import com.example.miskiparqueo.feature.reservation.presentation.ReservationConfirmViewModel
import com.example.miskiparqueo.feature.reservation.presentation.ReservationListViewModel
import com.example.miskiparqueo.feature.reservation.presentation.ReservationViewModel
import com.example.miskiparqueo.feature.reservation.presentation.model.ReservationConfirmArgs
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val reservationModule = module {
    single { ParkingExtrasDataSource() }
    single { ReservationLocalDataSource() }
    single<IReservationRepository> { ReservationRepositoryImpl(get(), get(), get()) }
    single { GetReservationDetailUseCase(get()) }
    single { ConfirmReservationUseCase(get()) }
    single { ObserveActiveReservationsUseCase(get()) }

    viewModel { (parkingId: String) ->
        ReservationViewModel(
            parkingId = parkingId,
            getReservationDetailUseCase = get()
        )
    }

    viewModel { (args: ReservationConfirmArgs) ->
        ReservationConfirmViewModel(
            userId = args.userId,
            parkingId = args.parkingId,
            dateIso = args.dateIso,
            entryTimeIso = args.entryTimeIso,
            exitTimeIso = args.exitTimeIso,
            totalCost = args.totalCost,
            getReservationDetailUseCase = get(),
            confirmReservationUseCase = get()
        )
    }

    viewModel { (userId: String) ->
        ReservationListViewModel(
            userId = userId,
            observeActiveReservationsUseCase = get()
        )
    }
}
