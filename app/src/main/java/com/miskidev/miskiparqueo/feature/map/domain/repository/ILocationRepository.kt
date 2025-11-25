package com.miskidev.miskiparqueo.feature.map.domain.repository

import com.google.android.gms.maps.model.LatLng

/**
 * Interfaz que define el contrato para obtener la ubicación actual del dispositivo.
 */
interface ILocationRepository {
    suspend fun getCurrentLocation(): Result<LatLng>
}