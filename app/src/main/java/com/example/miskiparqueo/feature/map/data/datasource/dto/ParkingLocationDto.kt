package com.example.miskiparqueo.feature.map.data.datasource.dto

data class ParkingLocationDto(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val pricePerHour: Double,
    val availableSpots: Int,
    val totalSpots: Int,
    val operatingHours: String
)