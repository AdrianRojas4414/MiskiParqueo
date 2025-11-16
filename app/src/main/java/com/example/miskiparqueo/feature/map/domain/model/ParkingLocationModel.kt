package com.example.miskiparqueo.feature.map.domain.model

data class ParkingLocationModel(
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

/**
 * Representa la entidad de dominio para la ubicación de un parqueo.
 * Este es el modelo que usará la UI y la lógica de negocio.
 *
 * @param id El identificador único del parqueo.
 * @param name El nombre comercial del parqueo (ej. "Parqueo Torres").
 * @param latitude La coordenada de latitud.
 * @param longitude La coordenada de longitud.
 */