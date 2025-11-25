package com.miskidev.miskiparqueo.feature.map.domain.model

data class OriginLocationModel(
    val latitude: Double,
    val longitude: Double,
    val address: String? = null
)