package com.example.miskiparqueo.feature.reservation.domain.usecases

import com.example.miskiparqueo.feature.reservation.domain.model.ReservationRecordModel
import com.example.miskiparqueo.feature.reservation.domain.repository.IReservationRepository
import kotlinx.coroutines.flow.Flow

class ObserveActiveReservationsUseCase(
    private val reservationRepository: IReservationRepository
) {
    operator fun invoke(userId: String): Flow<List<ReservationRecordModel>> {
        return reservationRepository.observeActiveReservations(userId)
    }
}
