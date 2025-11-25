package com.miskidev.miskiparqueo.feature.reservation.domain.usecases

import com.miskidev.miskiparqueo.feature.reservation.domain.model.ParkingReservationDetailModel
import com.miskidev.miskiparqueo.feature.reservation.domain.repository.IReservationRepository

class GetReservationDetailUseCase(
    private val reservationRepository: IReservationRepository
) {
    suspend operator fun invoke(parkingId: String): Result<ParkingReservationDetailModel> {
        return reservationRepository.getReservationDetail(parkingId)
    }
}
