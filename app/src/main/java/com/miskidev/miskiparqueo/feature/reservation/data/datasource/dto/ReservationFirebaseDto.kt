package com.miskidev.miskiparqueo.feature.reservation.data.datasource.dto

data class ReservationFirebaseDto(
    var id: String = "",
    var userId: String = "",
    var parkingId: String = "",
    var parkingName: String = "",
    var parkingAddress: String = "",
    var date: String = "",
    var entryTime: String = "",
    var exitTime: String = "",
    var totalCost: Double = 0.0,
    var status: String = "",
    var createdAt: Long = 0L
) {
    constructor() : this("", "", "", "", "", "", "", "", 0.0, "", 0L)
}