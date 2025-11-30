package com.miskidev.miskiparqueo.feature.map.domain.usecases

import com.miskidev.miskiparqueo.feature.map.domain.model.ParkingLocationModel
import com.miskidev.miskiparqueo.feature.map.domain.repository.IParkingRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchParkingsUseCaseTest {

    private val parkings = listOf(
        ParkingLocationModel(
            id = "1",
            name = "Central Parking",
            address = "Av. Principal 123",
            latitude = 0.0,
            longitude = 0.0,
            pricePerHour = 2.5,
            availableSpots = 5,
            totalSpots = 10,
            operatingHours = "24/7"
        ),
        ParkingLocationModel(
            id = "2",
            name = "Norte",
            address = "Calle Norte 456",
            latitude = 0.0,
            longitude = 0.0,
            pricePerHour = 3.0,
            availableSpots = 2,
            totalSpots = 8,
            operatingHours = "24/7"
        )
    )

    private val repository = object : IParkingRepository {
        override suspend fun getParkingLocations() = Result.success(parkings)
        override suspend fun getParkingById(parkingId: String) = Result.success(parkings.first())
    }

    @Test
    fun `consulta vacia devuelve todo`() = runTest {
        val useCase = SearchParkingsUseCase(repository)

        val result = useCase("")

        assertTrue(result.isSuccess)
        assertEquals(parkings, result.getOrNull())
    }

    @Test
    fun `consulta filtra nombre o direccion`() = runTest {
        val useCase = SearchParkingsUseCase(repository)

        val result = useCase("norte")

        assertEquals(listOf(parkings[1]), result.getOrNull())
    }
}
