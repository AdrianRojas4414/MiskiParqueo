package com.example.miskiparqueo.feature.map.domain.model

data class RouteInfoModel(
    val origin: OriginLocationModel,
    val destination: ParkingLocationModel,
    val estimatedTimeMinutes: Int,
    val distanceKm: Double
)