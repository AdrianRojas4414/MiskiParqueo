package com.miskidev.miskiparqueo.feature.reservation.presentation.model

data class ReservationConfirmArgs(
    val userId: String,
    val parkingId: String,
    val dateIso: String,
    val entryTimeIso: String,
    val exitTimeIso: String,
    val totalCost: Double
)
