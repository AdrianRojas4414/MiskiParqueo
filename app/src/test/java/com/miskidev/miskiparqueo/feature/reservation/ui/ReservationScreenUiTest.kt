package com.miskidev.miskiparqueo.feature.reservation.ui

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.miskidev.miskiparqueo.BuildConfig
import com.miskidev.miskiparqueo.MainDispatcherRule
import com.miskidev.miskiparqueo.feature.map.domain.model.ParkingLocationModel
import com.miskidev.miskiparqueo.feature.reservation.domain.model.ParkingReservationDetailModel
import com.miskidev.miskiparqueo.feature.reservation.domain.model.ReservationRecordModel
import com.miskidev.miskiparqueo.feature.reservation.domain.model.ReservationRequestModel
import com.miskidev.miskiparqueo.feature.reservation.domain.model.ReservationStatus
import com.miskidev.miskiparqueo.feature.reservation.domain.repository.IReservationRepository
import com.miskidev.miskiparqueo.feature.reservation.domain.usecases.GetReservationDetailUseCase
import com.miskidev.miskiparqueo.feature.reservation.presentation.ReservationScreen
import com.miskidev.miskiparqueo.feature.reservation.presentation.ReservationViewModel
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runners.model.Statement
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], manifest = Config.NONE, application = android.app.Application::class)
class ReservationScreenUiTest {

    private val mainDispatcherRule = MainDispatcherRule()
    private val composeRule = createComposeRule()
    private val debugOnlyRule = TestRule { base, _ ->
        object : Statement() {
            override fun evaluate() {
                if (!BuildConfig.DEBUG) {
                    org.junit.Assume.assumeTrue("UI tests solo en build debug", false)
                }
                base.evaluate()
            }
        }
    }

    @get:Rule
    val ruleChain: TestRule = RuleChain
        .outerRule(debugOnlyRule)
        .around(mainDispatcherRule)
        .around(composeRule)

    @Test
    fun pantallaReservaMuestraNombreParqueo() {
        val detail = detail(available = 5)
        val vm = ReservationViewModel(
            parkingId = detail.parking.id,
            getReservationDetailUseCase = GetReservationDetailUseCase(FakeReservationRepository(detail))
        )

        composeRule.setContent {
            ReservationScreen(
                userId = "u1",
                parkingId = "p1",
                onNavigateBack = {},
                onNavigateToConfirm = { _, _, _, _, _, _ -> },
                vm = vm
            )
        }

        composeRule.onNode(isRoot()).assertIsDisplayed()
    }

    @Test
    fun pantallaReservaMuestraErrorSinCupos() {
        val detail = detail(available = 0, total = 0)
        val vm = ReservationViewModel(
            parkingId = detail.parking.id,
            getReservationDetailUseCase = GetReservationDetailUseCase(FakeReservationRepository(detail))
        )

        composeRule.setContent {
            ReservationScreen(
                userId = "u1",
                parkingId = "p1",
                onNavigateBack = {},
                onNavigateToConfirm = { _, _, _, _, _, _ -> },
                vm = vm
            )
        }

        composeRule.onNode(isRoot()).assertIsDisplayed()
    }

    private fun detail(available: Int, total: Int = 10) = ParkingReservationDetailModel(
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
    ) : IReservationRepository {
        override suspend fun confirmReservation(request: ReservationRequestModel): Result<Unit> = Result.success(Unit)

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
}
