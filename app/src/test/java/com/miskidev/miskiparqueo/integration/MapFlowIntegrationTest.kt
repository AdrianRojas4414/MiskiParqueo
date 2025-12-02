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

    @Test
    fun `limpiar busqueda regresa a mapa con origen confirmado`() = runTest {
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

        advanceUntilIdle()
        viewModel.confirmOrigin(-17.4, -66.16, "Casa")
        viewModel.searchParkings("Central")
        advanceUntilIdle()
        viewModel.clearSearch()

        val state = viewModel.state.value
        assertTrue(state is MapViewModel.MapStateUI.ShowingMap)
        assertEquals(true, (state as MapViewModel.MapStateUI.ShowingMap).originConfirmed)
    }

    @Test
    fun `seleccionar sin origen produce error`() = runTest {
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

        advanceUntilIdle()
        viewModel.selectParking(parkings.first())

        val state = viewModel.state.value
        assertTrue(state is MapViewModel.MapStateUI.Error)
    }

    @Test
    fun `fallo al buscar cambia a estado error`() = runTest {
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
        val repository = FlakyParkingRepository(parkings)
        val viewModel = MapViewModel(
            getParkingLocationsUseCase = GetParkingLocationsUseCase(repository),
            searchParkingsUseCase = SearchParkingsUseCase(repository),
            calculateRouteUseCase = CalculateRouteUseCase(),
            dispatcher = dispatcher
        )

        advanceUntilIdle() // primera llamada exitosa
        viewModel.searchParkings("Central") // segunda llamada falla
        advanceUntilIdle()

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

    private class FlakyParkingRepository(
        private val parkings: List<ParkingLocationModel>
    ) : IParkingRepository {
        private var calls = 0

        override suspend fun getParkingLocations(): Result<List<ParkingLocationModel>> {
            calls++
            return if (calls == 1) Result.success(parkings)
            else Result.failure(IllegalStateException("fail search"))
        }

        override suspend fun getParkingById(parkingId: String): Result<ParkingLocationModel> =
            Result.success(parkings.first())
    }
}
