package com.miskidev.miskiparqueo.feature.reservation.domain

import com.miskidev.miskiparqueo.feature.map.domain.model.ParkingLocationModel
import com.miskidev.miskiparqueo.feature.reservation.domain.model.ParkingReservationDetailModel
import com.miskidev.miskiparqueo.feature.reservation.domain.model.ReservationRecordModel
import com.miskidev.miskiparqueo.feature.reservation.domain.model.ReservationRequestModel
import com.miskidev.miskiparqueo.feature.reservation.domain.model.ReservationStatus
import com.miskidev.miskiparqueo.feature.reservation.domain.repository.IReservationRepository
import com.miskidev.miskiparqueo.feature.reservation.domain.usecases.ConfirmReservationUseCase
import com.miskidev.miskiparqueo.feature.reservation.domain.usecases.GetReservationDetailUseCase
import com.miskidev.miskiparqueo.feature.reservation.domain.usecases.ObserveActiveReservationsUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReservationUseCasesTest {

    private val parking = ParkingLocationModel(
        id = "p1",
        name = "Parqueo Central",
        address = "Av. Principal 123",
        latitude = 0.0,
        longitude = 0.0,
        pricePerHour = 5.0,
        availableSpots = 3,
        totalSpots = 10,
        operatingHours = "24/7"
    )

    @Test
    fun `confirmacion de reserva delega`() = runTest {
        val repo = FakeReservationRepository()
        val useCase = ConfirmReservationUseCase(repo)
        val request = ReservationRequestModel(
            userId = "u1",
            parking = parking,
            date = java.time.LocalDate.of(2024, 1, 1),
            entryTime = java.time.LocalTime.of(10, 0),
            exitTime = java.time.LocalTime.of(12, 0),
            totalCost = 10.0
        )

        val result = useCase(request)

        assertTrue(result.isSuccess)
        assertEquals(request, repo.lastConfirmRequest)
    }

    @Test
    fun `detalle de reserva retorna datos`() = runTest {
        val repo = FakeReservationRepository(detail = ParkingReservationDetailModel(parking, "img", listOf("Techado"), "Desc"))
        val useCase = GetReservationDetailUseCase(repo)

        val result = useCase(parking.id)

        assertTrue(result.isSuccess)
        assertEquals(parking.id, result.getOrNull()?.parking?.id)
        assertEquals(parking.id, repo.lastDetailId)
    }

    @Test
    fun `observa reservas activas`() = runTest {
        val repo = FakeReservationRepository(records = listOf(ReservationRecordModel(
            id = "r1",
            userId = "u1",
            parkingId = parking.id,
            parkingName = parking.name,
            parkingAddress = parking.address,
            date = java.time.LocalDate.of(2024, 1, 1),
            entryTime = java.time.LocalTime.of(10, 0),
            exitTime = java.time.LocalTime.of(12, 0),
            totalCost = 10.0,
            status = ReservationStatus.ACTIVE,
            createdAt = 0L
        )))
        val useCase = ObserveActiveReservationsUseCase(repo)

        val flow = useCase("u1")
        val records = flow.first()

        assertEquals(1, records.size)
        assertEquals("r1", records.first().id)
        assertEquals("u1", repo.lastObserveUserId)
    }

    private class FakeReservationRepository(
        private val detail: ParkingReservationDetailModel? = null,
        private val records: List<ReservationRecordModel> = emptyList()
    ) : IReservationRepository {
        var lastConfirmRequest: ReservationRequestModel? = null
        var lastDetailId: String? = null
        var lastObserveUserId: String? = null

        override suspend fun confirmReservation(request: ReservationRequestModel): Result<Unit> {
            lastConfirmRequest = request
            return Result.success(Unit)
        }

        override suspend fun getReservationDetail(parkingId: String): Result<ParkingReservationDetailModel> {
            lastDetailId = parkingId
            return detail?.let { Result.success(it) } ?: Result.failure(IllegalStateException("No detail"))
        }

        override fun observeActiveReservations(userId: String) = flowOf(records.also { lastObserveUserId = userId })
    }
}
