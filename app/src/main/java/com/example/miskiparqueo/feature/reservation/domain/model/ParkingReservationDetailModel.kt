package com.example.miskiparqueo.feature.reservation.domain.model

import com.example.miskiparqueo.feature.map.domain.model.ParkingLocationModel

data class ParkingReservationDetailModel(
    val parking: ParkingLocationModel,
    val imageUrl: String,
    val amenities: List<String>,
    val description: String
)
