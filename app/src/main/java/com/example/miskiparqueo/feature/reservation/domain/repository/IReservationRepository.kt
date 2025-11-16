package com.example.miskiparqueo.feature.reservation.domain.repository

import com.example.miskiparqueo.feature.reservation.domain.model.ParkingReservationDetailModel
import com.example.miskiparqueo.feature.reservation.domain.model.ReservationRecordModel
import com.example.miskiparqueo.feature.reservation.domain.model.ReservationRequestModel
import kotlinx.coroutines.flow.Flow

interface IReservationRepository {
    suspend fun getReservationDetail(parkingId: String): Result<ParkingReservationDetailModel>
    suspend fun confirmReservation(request: ReservationRequestModel): Result<Unit>
    fun observeActiveReservations(userId: String): Flow<List<ReservationRecordModel>>
}
