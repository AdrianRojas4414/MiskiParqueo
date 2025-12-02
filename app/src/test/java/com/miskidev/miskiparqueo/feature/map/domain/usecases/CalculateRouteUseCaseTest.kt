package com.miskidev.miskiparqueo.feature.map.domain.usecases

import com.miskidev.miskiparqueo.feature.map.domain.model.OriginLocationModel
import com.miskidev.miskiparqueo.feature.map.domain.model.ParkingLocationModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.roundToInt

class CalculateRouteUseCaseTest {

    @Test
    fun `calcula distancia valida`() {
        val useCase = CalculateRouteUseCase()
        val origin = OriginLocationModel(-17.38, -66.16)
        val destination = parking(-17.39, -66.15)

        val route = useCase(origin, destination)

        assertTrue(route.distanceKm > 0)
        assertEquals(destination.id, route.destination.id)
    }

    @Test
    fun `estima tiempo aprox`() {
        val useCase = CalculateRouteUseCase()
        val origin = OriginLocationModel(0.0, 0.0)
        val destination = parking(0.0, 0.27) // ~30km at equator

        val route = useCase(origin, destination)

        // 30 km at 30 km/h ≈ 60 minutes, allow small rounding diff
        assertTrue(route.estimatedTimeMinutes in 58..62)
    }

    private fun parking(lat: Double, lon: Double) = ParkingLocationModel(
        id = "1",
        name = "Test",
        address = "addr",
        latitude = lat,
        longitude = lon,
        pricePerHour = 1.0,
        availableSpots = 1,
        totalSpots = 1,
        operatingHours = "24/7"
    )
}
