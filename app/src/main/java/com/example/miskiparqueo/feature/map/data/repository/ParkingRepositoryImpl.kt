package com.example.miskiparqueo.feature.map.data.repository

import com.example.miskiparqueo.feature.map.data.datasource.ParkingLocationDataSource
import com.example.miskiparqueo.feature.map.domain.model.ParkingLocationModel
import com.example.miskiparqueo.feature.map.domain.repository.IParkingRepository

class ParkingRepositoryImpl(
    private val dataSource: ParkingLocationDataSource
) : IParkingRepository {

    override suspend fun getParkingLocations(): Result<List<ParkingLocationModel>> {
        return try {
            dataSource.getParkingLocations().map { dtoList ->
                dtoList.map { dto ->
                    // Mapear DTO a modelo de dominio
                    ParkingLocationModel(
                        id = dto.id,
                        name = dto.name,
                        address = dto.address,
                        latitude = dto.latitude,
                        longitude = dto.longitude,
                        pricePerHour = dto.pricePerHour,
                        availableSpots = dto.availableSpots,
                        totalSpots = dto.totalSpots,
                        operatingHours = dto.operatingHours
                    )
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}