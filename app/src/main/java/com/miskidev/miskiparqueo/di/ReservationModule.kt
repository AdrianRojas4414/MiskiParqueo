package com.miskidev.miskiparqueo.di

import com.miskidev.miskiparqueo.feature.reservation.data.datasource.ParkingExtrasDataSource
import com.miskidev.miskiparqueo.feature.reservation.data.datasource.ReservationFirebaseDataSource
import com.miskidev.miskiparqueo.feature.reservation.data.repository.ReservationRepositoryImpl
import com.miskidev.miskiparqueo.feature.reservation.domain.repository.IReservationRepository
import com.miskidev.miskiparqueo.feature.reservation.domain.usecases.ConfirmReservationUseCase
import com.miskidev.miskiparqueo.feature.reservation.domain.usecases.GetReservationDetailUseCase
import com.miskidev.miskiparqueo.feature.reservation.domain.usecases.ObserveActiveReservationsUseCase
import com.miskidev.miskiparqueo.feature.reservation.presentation.ReservationConfirmViewModel
import com.miskidev.miskiparqueo.feature.reservation.presentation.ReservationListViewModel
import com.miskidev.miskiparqueo.feature.reservation.presentation.ReservationViewModel
import com.miskidev.miskiparqueo.feature.reservation.presentation.model.ReservationConfirmArgs
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val reservationModule = module {
    // DataSources
    single { ParkingExtrasDataSource() }
    single { ReservationFirebaseDataSource() } // CAMBIADO: de Local a Firebase

    // Repository
    single<IReservationRepository> {
        ReservationRepositoryImpl(get(), get(), get())
    }

    // UseCases
    single { GetReservationDetailUseCase(get()) }
    single { ConfirmReservationUseCase(get()) }
    single { ObserveActiveReservationsUseCase(get()) }

    // ViewModels
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
