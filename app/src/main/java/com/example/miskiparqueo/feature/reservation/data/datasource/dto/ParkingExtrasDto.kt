package com.example.miskiparqueo.feature.reservation.data.datasource.dto

/**
 * [imageName] debe coincidir con el nombre del recurso (sin extensión) dentro de
 * `app/src/main/res/drawable`. Ej.: si subes `parqueo_centro.png`, coloca "parqueo_centro".
 */
data class ParkingExtrasDto(
    val imageName: String,
    val amenities: List<String>,
    val description: String
)
