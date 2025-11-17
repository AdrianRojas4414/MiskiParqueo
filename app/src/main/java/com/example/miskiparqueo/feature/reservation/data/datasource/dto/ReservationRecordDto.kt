package com.example.miskiparqueo.feature.reservation.data.datasource.dto

data class ReservationRecordDto(
    val id: String,
    val userId: String,
    val parkingId: String,
    val parkingName: String,
    val parkingAddress: String,
    val date: String,
    val entryTime: String,
    val exitTime: String,
    val totalCost: Double,
    val status: String,
    val createdAt: Long
)
