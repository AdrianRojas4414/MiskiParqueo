package com.miskidev.miskiparqueo.integration

import com.miskidev.miskiparqueo.MainDispatcherRule
import com.miskidev.miskiparqueo.feature.map.domain.model.ParkingLocationModel
import com.miskidev.miskiparqueo.feature.reservation.domain.model.ParkingReservationDetailModel
import com.miskidev.miskiparqueo.feature.reservation.domain.model.ReservationRecordModel
import com.miskidev.miskiparqueo.feature.reservation.domain.model.ReservationRequestModel
import com.miskidev.miskiparqueo.feature.reservation.domain.model.ReservationStatus
import com.miskidev.miskiparqueo.feature.reservation.domain.repository.IReservationRepository
import com.miskidev.miskiparqueo.feature.reservation.domain.usecases.GetReservationDetailUseCase
import com.miskidev.miskiparqueo.feature.reservation.presentation.ReservationViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReservationFlowIntegrationTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `carga detalle y calcula costo`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val detail = detail(available = 3, total = 10)
        val viewModel = ReservationViewModel(
            parkingId = detail.parking.id,
            getReservationDetailUseCase = GetReservationDetailUseCase(SuccessReservationRepository(detail)),
            dispatcher = dispatcher
        )

        advanceUntilIdle()
        val state = viewModel.uiState.dropWhile { it.isLoading }.first()

        assertNotNull(state.detail)
        assertTrue(state.totalCost > 0.0)
        assertTrue(state.validationError == null)
    }

    @Test
    fun `sin cupos marca validacion`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val detail = detail(available = 0, total = 0)
        val viewModel = ReservationViewModel(
            parkingId = detail.parking.id,
            getReservationDetailUseCase = GetReservationDetailUseCase(SuccessReservationRepository(detail)),
            dispatcher = dispatcher
        )

        advanceUntilIdle()
        val state = viewModel.uiState.dropWhile { it.isLoading }.first()

        assertTrue(!state.hasAvailableSpots)
        assertTrue(state.validationError?.contains("cupo", ignoreCase = true) == true)
    }

    @Test
    fun `error de repositorio propaga mensaje`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val viewModel = ReservationViewModel(
            parkingId = "missing",
            getReservationDetailUseCase = GetReservationDetailUseCase(FailingReservationRepository()),
            dispatcher = dispatcher
        )

        advanceUntilIdle()
        val state = viewModel.uiState.dropWhile { it.isLoading }.first()

        assertTrue(state.errorMessage?.contains("fail", ignoreCase = true) == true)
    }

    private fun detail(available: Int, total: Int) = ParkingReservationDetailModel(
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

    private class SuccessReservationRepository(
        private val detail: ParkingReservationDetailModel
    ) : IReservationRepository {
        override suspend fun confirmReservation(request: ReservationRequestModel): Result<Unit> =
            Result.success(Unit)

        override suspend fun getReservationDetail(parkingId: String): Result<ParkingReservationDetailModel> =
            Result.success(detail)

        override fun observeActiveReservations(userId: String) = flowOf(
            listOf(
                ReservationRecordModel(
                    id = "r1",
                    userId = userId,
                    parkingId = detail.parking.id,
                    parkingName = detail.parking.name,
                    parkingAddress = detail.parking.address,
                    date = java.time.LocalDate.now(),
                    entryTime = java.time.LocalTime.of(10, 0),
                    exitTime = java.time.LocalTime.of(12, 0),
                    totalCost = 10.0,
                    status = ReservationStatus.ACTIVE,
                    createdAt = 0L
                )
            )
        )
    }

    private class FailingReservationRepository : IReservationRepository {
        override suspend fun confirmReservation(request: ReservationRequestModel): Result<Unit> =
            Result.success(Unit)

        override suspend fun getReservationDetail(parkingId: String): Result<ParkingReservationDetailModel> =
            Result.failure(IllegalStateException("fail"))

        override fun observeActiveReservations(userId: String) = flowOf(emptyList<ReservationRecordModel>())
    }
}
