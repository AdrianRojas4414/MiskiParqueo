package com.example.miskiparqueo.feature.map.domain.usecases

import com.example.miskiparqueo.feature.map.domain.model.ParkingLocationModel
import com.example.miskiparqueo.feature.map.domain.repository.IParkingRepository

class SearchParkingsUseCase(
    private val repository: IParkingRepository
) {
    suspend operator fun invoke(query: String): Result<List<ParkingLocationModel>> {
        if (query.isBlank()) {
            return repository.getParkingLocations()
        }

        return repository.getParkingLocations().map { parkings ->
            parkings.filter { parking ->
                parking.name.contains(query, ignoreCase = true) ||
                        parking.address.contains(query, ignoreCase = true)
            }
        }
    }
}