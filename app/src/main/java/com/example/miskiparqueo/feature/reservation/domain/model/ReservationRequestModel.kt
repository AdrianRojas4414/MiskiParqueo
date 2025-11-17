package com.example.miskiparqueo.feature.reservation.domain.model

import com.example.miskiparqueo.feature.map.domain.model.ParkingLocationModel
import java.time.LocalDate
import java.time.LocalTime

data class ReservationRequestModel(
    val userId: String,
    val parking: ParkingLocationModel,
    val date: LocalDate,
    val entryTime: LocalTime,
    val exitTime: LocalTime,
    val totalCost: Double
)
