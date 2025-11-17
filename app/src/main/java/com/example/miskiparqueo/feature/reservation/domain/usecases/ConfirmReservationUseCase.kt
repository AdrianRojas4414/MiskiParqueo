package com.example.miskiparqueo.feature.reservation.domain.usecases

import com.example.miskiparqueo.feature.reservation.domain.model.ReservationRequestModel
import com.example.miskiparqueo.feature.reservation.domain.repository.IReservationRepository

class ConfirmReservationUseCase(
    private val reservationRepository: IReservationRepository
) {
    suspend operator fun invoke(request: ReservationRequestModel): Result<Unit> {
        return reservationRepository.confirmReservation(request)
    }
}
