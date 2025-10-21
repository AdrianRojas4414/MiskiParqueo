package com.example.miskiparqueo.feature.map.domain.repository

import com.example.miskiparqueo.feature.map.domain.model.ParkingLocationModel

interface IParkingRepository {
    suspend fun getParkingLocations(): Result<List<ParkingLocationModel>>
}