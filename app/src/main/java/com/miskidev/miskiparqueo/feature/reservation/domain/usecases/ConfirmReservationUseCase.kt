package com.miskidev.miskiparqueo.feature.reservation.domain.usecases

import com.miskidev.miskiparqueo.feature.reservation.domain.model.ReservationRequestModel
import com.miskidev.miskiparqueo.feature.reservation.domain.repository.IReservationRepository

class ConfirmReservationUseCase(
    private val reservationRepository: IReservationRepository
) {
    suspend operator fun invoke(request: ReservationRequestModel): Result<Unit> {
        return reservationRepository.confirmReservation(request)
    }
}
