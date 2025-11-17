package com.example.miskiparqueo.feature.reservation.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miskiparqueo.feature.reservation.domain.model.ParkingReservationDetailModel
import com.example.miskiparqueo.feature.reservation.domain.usecases.GetReservationDetailUseCase
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlin.math.ceil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReservationViewModel(
    private val parkingId: String,
    private val getReservationDetailUseCase: GetReservationDetailUseCase
) : ViewModel() {

    data class ReservationUiState(
        val isLoading: Boolean = true,
        val errorMessage: String? = null,
        val detail: ParkingReservationDetailModel? = null,
        val selectedDate: LocalDate = LocalDate.now(),
        val entryTime: LocalTime = LocalTime.now(),
        val exitTime: LocalTime = LocalTime.now().plusHours(2),
        val totalCost: Double = 0.0,
        val validationError: String? = null
    )

    private val _uiState = MutableStateFlow(ReservationUiState())
    val uiState: StateFlow<ReservationUiState> = _uiState.asStateFlow()

    init {
        loadReservationDetail()
    }

    fun reload() {
        loadReservationDetail()
    }

    fun onDateSelected(newDate: LocalDate) {
        _uiState.value = _uiState.value.copy(selectedDate = newDate)
    }

    fun onEntryTimeSelected(time: LocalTime) {
        val detail = _uiState.value.detail ?: return
        val (totalCost, validationError) = calculateCost(detail, time, _uiState.value.exitTime)
        _uiState.value = _uiState.value.copy(
            entryTime = time,
            totalCost = totalCost,
            validationError = validationError
        )
    }

    fun onExitTimeSelected(time: LocalTime) {
        val detail = _uiState.value.detail ?: return
        val (totalCost, validationError) = calculateCost(detail, _uiState.value.entryTime, time)
        _uiState.value = _uiState.value.copy(
            exitTime = time,
            totalCost = totalCost,
            validationError = validationError
        )
    }

    private fun loadReservationDetail() {
        viewModelScope.launch {
            _uiState.value = ReservationUiState(isLoading = true)
            val defaultEntry = LocalTime.now().truncatedTo(ChronoUnit.HOURS)
            val defaultExit = defaultEntry.plusHours(2)
            val defaultDate = LocalDate.now(ZoneId.systemDefault())

            val result = getReservationDetailUseCase(parkingId)
            result.fold(
                onSuccess = { detail ->
                    val (totalCost, validationError) = calculateCost(detail, defaultEntry, defaultExit)
                    _uiState.value = ReservationUiState(
                        isLoading = false,
                        detail = detail,
                        selectedDate = defaultDate,
                        entryTime = defaultEntry,
                        exitTime = defaultExit,
                        totalCost = totalCost,
                        validationError = validationError
                    )
                },
                onFailure = { error ->
                    _uiState.value = ReservationUiState(
                        isLoading = false,
                        errorMessage = error.message ?: "No se pudo cargar la informaci\u00f3n"
                    )
                }
            )
        }
    }

    private fun calculateCost(
        detail: ParkingReservationDetailModel,
        entryTime: LocalTime,
        exitTime: LocalTime
    ): Pair<Double, String?> {
        val minutes = java.time.Duration.between(entryTime, exitTime).toMinutes()
        if (minutes <= 0) {
            return 0.0 to "La hora de salida debe ser mayor a la de entrada"
        }
        val hours = ceil(minutes / 60.0)
        val total = hours * detail.parking.pricePerHour
        return total to null
    }
}
