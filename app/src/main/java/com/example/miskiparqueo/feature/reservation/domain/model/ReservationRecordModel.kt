package com.example.miskiparqueo.feature.reservation.domain.model

import java.time.LocalDate
import java.time.LocalTime

data class ReservationRecordModel(
    val id: String,
    val userId: String,
    val parkingId: String,
    val parkingName: String,
    val parkingAddress: String,
    val date: LocalDate,
    val entryTime: LocalTime,
    val exitTime: LocalTime,
    val totalCost: Double,
    val status: ReservationStatus,
    val createdAt: Long
)
