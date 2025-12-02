package com.miskidev.miskiparqueo.feature.reservation.presentation

import com.miskidev.miskiparqueo.MainDispatcherRule
import com.miskidev.miskiparqueo.feature.map.domain.model.ParkingLocationModel
import com.miskidev.miskiparqueo.feature.reservation.domain.model.ParkingReservationDetailModel
import com.miskidev.miskiparqueo.feature.reservation.domain.usecases.GetReservationDetailUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class ReservationViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `carga detalle exitoso actualiza estado`() = runTest {
        val dispatcher = UnconfinedTestDispatcher(testScheduler)
        val detail = sampleDetail(available = 5)
        val viewModel = ReservationViewModel(
            parkingId = detail.parking.id,
            getReservationDetailUseCase = GetReservationDetailUseCase(FakeReservationRepository(detail)),
            dispatcher = dispatcher
        )

        viewModel.reload()
        val state = viewModel.uiState.dropWhile { it.isLoading }.first()
        assertNotNull(state.detail)
        assertEquals("Parqueo Central", state.detail?.parking?.name)
    }

    @Test
    fun `sin cupos muestra validacion`() = runTest {
        val dispatcher = UnconfinedTestDispatcher(testScheduler)
        val detail = sampleDetail(available = 0, total = 0)
        val viewModel = ReservationViewModel(
            parkingId = detail.parking.id,
            getReservationDetailUseCase = GetReservationDetailUseCase(FakeReservationRepository(detail)),
            dispatcher = dispatcher
        )

        viewModel.reload()
        val state = viewModel.uiState.dropWhile { it.isLoading }.first()

        assertFalse(state.hasAvailableSpots)
        assertNotNull(state.validationError)
        assertTrue(state.validationError!!.contains("cupo", ignoreCase = true))
    }

    @Test
    fun `hora de salida menor marca error`() = runTest {
        val dispatcher = UnconfinedTestDispatcher(testScheduler)
        val detail = sampleDetail(available = 2)
        val viewModel = ReservationViewModel(
            parkingId = detail.parking.id,
            getReservationDetailUseCase = GetReservationDetailUseCase(FakeReservationRepository(detail)),
            dispatcher = dispatcher
        )

        viewModel.reload()
        val entry = viewModel.uiState.dropWhile { it.isLoading }.first().entryTime
        viewModel.onExitTimeSelected(entry.minusHours(1))

        val state = viewModel.uiState.value
        assertEquals(0.0, state.totalCost, 0.0)
        assertTrue(state.validationError?.contains("salida", ignoreCase = true) == true)
    }

    @Test
    fun `error al cargar deja mensaje de error`() = runTest {
        val dispatcher = UnconfinedTestDispatcher(testScheduler)
        val viewModel = ReservationViewModel(
            parkingId = "p1",
            getReservationDetailUseCase = GetReservationDetailUseCase(FailingReservationRepository()),
            dispatcher = dispatcher
        )

        viewModel.reload()
        val state = viewModel.uiState.dropWhile { it.isLoading }.first()

        assertTrue(state.errorMessage?.contains("fail") == true)
    }

    @Test
    fun `recalcula costo al cambiar hora de entrada`() = runTest {
        val dispatcher = UnconfinedTestDispatcher(testScheduler)
        val detail = sampleDetail(available = 2)
        val viewModel = ReservationViewModel(
            parkingId = detail.parking.id,
            getReservationDetailUseCase = GetReservationDetailUseCase(FakeReservationRepository(detail)),
            dispatcher = dispatcher
        )

        viewModel.reload()
        val baseState = viewModel.uiState.dropWhile { it.isLoading }.first()
        val newEntry = baseState.entryTime.plusHours(1)
        viewModel.onEntryTimeSelected(newEntry)
        val updated = viewModel.uiState.value

        assertTrue(updated.totalCost >= 0.0)
        assertEquals(newEntry, updated.entryTime)
    }

    private fun sampleDetail(available: Int, total: Int = 10) = ParkingReservationDetailModel(
        parking = ParkingLocationModel(
            id = "p1",
            name = "Parqueo Central",
            address = "Av. Principal 123",
            latitude = 0.0,
            longitude = 0.0,
            pricePerHour = 5.0,
            availableSpots = available,
            totalSpots = total,
            operatingHours = "24/7"
        ),
        imageName = "img",
        amenities = listOf("Techado"),
        description = "Seguro"
    )

    private class FakeReservationRepository(
        private val detail: ParkingReservationDetailModel
    ) : com.miskidev.miskiparqueo.feature.reservation.domain.repository.IReservationRepository {
        override suspend fun confirmReservation(request: com.miskidev.miskiparqueo.feature.reservation.domain.model.ReservationRequestModel): Result<Unit> =
            Result.success(Unit)

        override suspend fun getReservationDetail(parkingId: String): Result<ParkingReservationDetailModel> =
            Result.success(detail)

        override fun observeActiveReservations(userId: String) =
            kotlinx.coroutines.flow.flowOf(emptyList<com.miskidev.miskiparqueo.feature.reservation.domain.model.ReservationRecordModel>())
    }

    private class FailingReservationRepository : com.miskidev.miskiparqueo.feature.reservation.domain.repository.IReservationRepository {
        override suspend fun confirmReservation(request: com.miskidev.miskiparqueo.feature.reservation.domain.model.ReservationRequestModel): Result<Unit> =
            Result.success(Unit)

        override suspend fun getReservationDetail(parkingId: String): Result<ParkingReservationDetailModel> =
            Result.failure(IllegalStateException("fail"))

        override fun observeActiveReservations(userId: String) =
            kotlinx.coroutines.flow.flowOf(emptyList<com.miskidev.miskiparqueo.feature.reservation.domain.model.ReservationRecordModel>())
    }
}
