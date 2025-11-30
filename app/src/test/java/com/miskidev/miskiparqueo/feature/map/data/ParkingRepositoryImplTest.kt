package com.miskidev.miskiparqueo.feature.map.data

import com.miskidev.miskiparqueo.feature.map.data.datasource.dto.ParkingLocationDto
import com.miskidev.miskiparqueo.feature.map.data.repository.ParkingRepositoryImpl
import kotlinx.coroutines.runBlocking
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ParkingRepositoryImplTest {

    @Test
    fun `mapea dto a modelo de dominio`() = runBlocking {
        val dto = ParkingLocationDto(
            id = "p1",
            name = "Parqueo Central",
            address = "Av 123",
            latitude = 1.0,
            longitude = 2.0,
            pricePerHour = 5.0,
            availableSpots = 3,
            totalSpots = 10,
            operatingHours = "24/7"
        )
        val dataSource = mockk<com.miskidev.miskiparqueo.feature.map.data.datasource.ParkingLocationDataSource>()
        coEvery { dataSource.getParkingLocations() } returns Result.success(listOf(dto))
        val repo = ParkingRepositoryImpl(dataSource)

        val result = repo.getParkingLocations().getOrThrow().first()

        assertEquals(dto.id, result.id)
        assertEquals(dto.name, result.name)
        assertEquals(dto.availableSpots, result.availableSpots)
    }

    @Test
    fun `getParkingById retorna error si no existe`() = runBlocking {
        val dataSource = mockk<com.miskidev.miskiparqueo.feature.map.data.datasource.ParkingLocationDataSource>()
        coEvery { dataSource.getParkingLocations() } returns Result.success(emptyList())
        val repo = ParkingRepositoryImpl(dataSource)

        val result = repo.getParkingById("missing")

        assertTrue(result.isFailure)
    }
}
