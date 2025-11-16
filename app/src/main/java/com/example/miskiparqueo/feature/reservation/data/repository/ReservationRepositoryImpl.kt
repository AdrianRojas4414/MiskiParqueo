package com.example.miskiparqueo.feature.reservation.data.repository

import com.example.miskiparqueo.feature.map.domain.repository.IParkingRepository
import com.example.miskiparqueo.feature.reservation.data.datasource.ParkingExtrasDataSource
import com.example.miskiparqueo.feature.reservation.domain.model.ParkingReservationDetailModel
import com.example.miskiparqueo.feature.reservation.domain.repository.IReservationRepository

class ReservationRepositoryImpl(
    private val parkingRepository: IParkingRepository,
    private val parkingExtrasDataSource: ParkingExtrasDataSource
) : IReservationRepository {

    override suspend fun getReservationDetail(parkingId: String): Result<ParkingReservationDetailModel> {
        val parkingResult = parkingRepository.getParkingById(parkingId)
        val extrasResult = parkingExtrasDataSource.getParkingExtras(parkingId)

        return parkingResult.fold(
            onSuccess = { parking ->
                extrasResult.fold(
                    onSuccess = { extras ->
                        Result.success(
                            ParkingReservationDetailModel(
                                parking = parking,
                                imageUrl = extras.imageUrl,
                                amenities = extras.amenities,
                                description = extras.description
                            )
                        )
                    },
                    onFailure = { Result.failure(it) }
                )
            },
            onFailure = { Result.failure(it) }
        )
    }
}
