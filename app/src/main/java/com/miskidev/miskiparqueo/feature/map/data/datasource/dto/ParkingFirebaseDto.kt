package com.miskidev.miskiparqueo.feature.map.data.datasource.dto

data class ParkingFirebaseDto(
    var id: String = "",
    var name: String = "",
    var address: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var pricePerHour: Double = 0.0,
    var availableSpots: Int = 0,
    var totalSpots: Int = 0,
    var operatingHours: String = "",
    var imageName: String = "",
    var description: String = "",
    var amenities: List<String> = emptyList()
) {
    // Constructor sin argumentos requerido por Firebase
    constructor() : this("", "", "", 0.0, 0.0, 0.0, 0, 0, "", "", "", emptyList())
}