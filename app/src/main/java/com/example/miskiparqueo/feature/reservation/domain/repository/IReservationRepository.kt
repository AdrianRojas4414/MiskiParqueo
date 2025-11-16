package com.example.miskiparqueo.feature.reservation.domain.repository

import com.example.miskiparqueo.feature.reservation.domain.model.ParkingReservationDetailModel

interface IReservationRepository {
    suspend fun getReservationDetail(parkingId: String): Result<ParkingReservationDetailModel>
}
