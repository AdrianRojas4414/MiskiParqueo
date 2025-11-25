package com.miskidev.miskiparqueo.feature.map.domain.usecases

import com.miskidev.miskiparqueo.feature.map.domain.model.OriginLocationModel
import com.miskidev.miskiparqueo.feature.map.domain.model.ParkingLocationModel
import com.miskidev.miskiparqueo.feature.map.domain.model.RouteInfoModel
import kotlin.math.*

class CalculateRouteUseCase {
    operator fun invoke(
        origin: OriginLocationModel,
        destination: ParkingLocationModel
    ): RouteInfoModel {
        // Calcular distancia usando fórmula Haversine
        val distance = calculateDistance(
            origin.latitude, origin.longitude,
            destination.latitude, destination.longitude
        )

        // Estimar tiempo (asumiendo velocidad promedio de 30 km/h en ciudad)
        val estimatedTime = (distance / 30.0 * 60).roundToInt() // minutos

        return RouteInfoModel(
            origin = origin,
            destination = destination,
            estimatedTimeMinutes = estimatedTime,
            distanceKm = distance
        )
    }

    private fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val earthRadius = 6371.0 // Radio de la Tierra en km

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }
}