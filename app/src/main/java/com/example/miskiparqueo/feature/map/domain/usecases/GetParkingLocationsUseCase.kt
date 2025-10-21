package com.example.miskiparqueo.feature.map.domain.usecases

import com.example.miskiparqueo.feature.map.domain.model.ParkingLocationModel
import com.example.miskiparqueo.feature.map.domain.repository.IParkingRepository

class GetParkingLocationsUseCase(
    private val repository: IParkingRepository
) {
    suspend operator fun invoke(): Result<List<ParkingLocationModel>> {
        return repository.getParkingLocations()
    }
}