package com.example.miskiparqueo.feature.reservation.domain.model

import com.example.miskiparqueo.feature.map.domain.model.ParkingLocationModel

data class ParkingReservationDetailModel(
    val parking: ParkingLocationModel,
    val imageName: String,
    val amenities: List<String>,
    val description: String
)
