package com.miskidev.miskiparqueo.feature.map.presentation

import com.miskidev.miskiparqueo.MainDispatcherRule
import com.miskidev.miskiparqueo.feature.map.domain.model.ParkingLocationModel
import com.miskidev.miskiparqueo.feature.map.domain.repository.IParkingRepository
import com.miskidev.miskiparqueo.feature.map.domain.usecases.CalculateRouteUseCase
import com.miskidev.miskiparqueo.feature.map.domain.usecases.GetParkingLocationsUseCase
import com.miskidev.miskiparqueo.feature.map.domain.usecases.SearchParkingsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MapViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val parking = ParkingLocationModel(
        id = "1",
        name = "Central Parking",
        address = "Av. Principal 123",
        latitude = -17.39,
        longitude = -66.15,
        pricePerHour = 3.5,
        availableSpots = 5,
        totalSpots = 10,
        operatingHours = "24/7"
    )

    @Test
    fun `carga parqueos al iniciar`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val fakeRepository = FakeParkingRepository(listOf(parking))
        val viewModel = MapViewModel(
            getParkingLocationsUseCase = GetParkingLocationsUseCase(fakeRepository),
            searchParkingsUseCase = SearchParkingsUseCase(fakeRepository),
            calculateRouteUseCase = CalculateRouteUseCase(),
            dispatcher = dispatcher
        )

        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is MapViewModel.MapStateUI.ShowingMap)
        assertEquals(listOf(parking), (state as MapViewModel.MapStateUI.ShowingMap).parkings)
    }

    @Test
    fun `seleccionar con origen devuelve ruta`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val fakeRepository = FakeParkingRepository(listOf(parking))
        val viewModel = MapViewModel(
            getParkingLocationsUseCase = GetParkingLocationsUseCase(fakeRepository),
            searchParkingsUseCase = SearchParkingsUseCase(fakeRepository),
            calculateRouteUseCase = CalculateRouteUseCase(),
            dispatcher = dispatcher
        )

        advanceUntilIdle()
        viewModel.confirmOrigin(latitude = -17.4, longitude = -66.16, address = "Casa")
        viewModel.selectParking(parking)

        val state = viewModel.state.value
        assertTrue(state is MapViewModel.MapStateUI.ParkingSelected)
        val routeInfo = (state as MapViewModel.MapStateUI.ParkingSelected).routeInfo
        assertEquals(parking.id, routeInfo.destination.id)
        assertTrue(routeInfo.distanceKm > 0.0)
    }

    @Test
    fun `busqueda actualiza estado`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val fakeRepository = FakeParkingRepository(listOf(parking))
        val viewModel = MapViewModel(
            getParkingLocationsUseCase = GetParkingLocationsUseCase(fakeRepository),
            searchParkingsUseCase = SearchParkingsUseCase(fakeRepository),
            calculateRouteUseCase = CalculateRouteUseCase(),
            dispatcher = dispatcher
        )

        advanceUntilIdle()
        viewModel.searchParkings("Central")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is MapViewModel.MapStateUI.Searching)
        assertEquals(1, (state as MapViewModel.MapStateUI.Searching).filteredParkings.size)
    }

    @Test
    fun `seleccionar sin origen error`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val fakeRepository = FakeParkingRepository(listOf(parking))
        val viewModel = MapViewModel(
            getParkingLocationsUseCase = GetParkingLocationsUseCase(fakeRepository),
            searchParkingsUseCase = SearchParkingsUseCase(fakeRepository),
            calculateRouteUseCase = CalculateRouteUseCase(),
            dispatcher = dispatcher
        )

        advanceUntilIdle()
        viewModel.selectParking(parking)

        val state = viewModel.state.value
        assertTrue(state is MapViewModel.MapStateUI.Error)
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
