package com.miskidev.miskiparqueo.integration

import com.miskidev.miskiparqueo.MainDispatcherRule
import com.miskidev.miskiparqueo.feature.map.domain.model.ParkingLocationModel
import com.miskidev.miskiparqueo.feature.map.domain.repository.IParkingRepository
import com.miskidev.miskiparqueo.feature.map.domain.usecases.CalculateRouteUseCase
import com.miskidev.miskiparqueo.feature.map.domain.usecases.GetParkingLocationsUseCase
import com.miskidev.miskiparqueo.feature.map.domain.usecases.SearchParkingsUseCase
import com.miskidev.miskiparqueo.feature.map.presentation.MapViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MapFlowIntegrationTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `flujo completo carga, filtra y selecciona parqueo con ruta`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val parkings = listOf(
            ParkingLocationModel(
                id = "p1",
                name = "Parqueo Central",
                address = "Av. Principal 123",
                latitude = -17.39,
                longitude = -66.15,
                pricePerHour = 3.5,
                availableSpots = 5,
                totalSpots = 10,
                operatingHours = "24/7"
            )
        )
        val repository = FakeParkingRepository(parkings)
        val viewModel = MapViewModel(
            getParkingLocationsUseCase = GetParkingLocationsUseCase(repository),
            searchParkingsUseCase = SearchParkingsUseCase(repository),
            calculateRouteUseCase = CalculateRouteUseCase(),
            dispatcher = dispatcher
        )

        advanceUntilIdle() // carga inicial
        viewModel.confirmOrigin(latitude = -17.4, longitude = -66.16, address = "Casa")
        viewModel.searchParkings("Central")
        advanceUntilIdle() // termina filtrado antes de seleccionar
        viewModel.selectParking(parkings.first())
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is MapViewModel.MapStateUI.ParkingSelected)
        val selected = state as MapViewModel.MapStateUI.ParkingSelected
        assertEquals(parkings.first().id, selected.routeInfo.destination.id)
        assertEquals(parkings.first(), selected.routeInfo.destination)
        assertTrue(selected.routeInfo.distanceKm > 0.0)
        assertTrue(selected.routeInfo.estimatedTimeMinutes > 0)
    }

    private class FakeParkingRepository(
        private val parkings: List<ParkingLocationModel>
    ) : IParkingRepository {
        override suspend fun getParkingLocations(): Result<List<ParkingLocationModel>> = Result.success(parkings)

        override suspend fun getParkingById(parkingId: String): Result<ParkingLocationModel> =
            parkings.firstOrNull { it.id == parkingId }?.let { Result.success(it) }
                ?: Result.failure(NoSuchElementException("Parking not found"))
    }
}
