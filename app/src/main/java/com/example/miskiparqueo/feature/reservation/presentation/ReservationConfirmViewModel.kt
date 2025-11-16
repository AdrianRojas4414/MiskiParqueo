package com.example.miskiparqueo.feature.reservation.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miskiparqueo.feature.reservation.domain.model.ParkingReservationDetailModel
import com.example.miskiparqueo.feature.reservation.domain.model.ReservationRequestModel
import com.example.miskiparqueo.feature.reservation.domain.usecases.ConfirmReservationUseCase
import com.example.miskiparqueo.feature.reservation.domain.usecases.GetReservationDetailUseCase
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReservationConfirmViewModel(
    private val userId: String,
    private val parkingId: String,
    dateIso: String,
    entryTimeIso: String,
    exitTimeIso: String,
    private val totalCost: Double,
    private val getReservationDetailUseCase: GetReservationDetailUseCase,
    private val confirmReservationUseCase: ConfirmReservationUseCase
) : ViewModel() {

    data class ConfirmUiState(
        val isLoading: Boolean = true,
        val isConfirming: Boolean = false,
        val detail: ParkingReservationDetailModel? = null,
        val date: LocalDate,
        val entryTime: LocalTime,
        val exitTime: LocalTime,
        val totalCost: Double,
        val success: Boolean = false,
        val errorMessage: String? = null
    )

    private val reservationDate = LocalDate.parse(dateIso)
    private val reservationEntryTime = LocalTime.parse(entryTimeIso)
    private val reservationExitTime = LocalTime.parse(exitTimeIso)

    private val _uiState = MutableStateFlow(
        ConfirmUiState(
            date = reservationDate,
            entryTime = reservationEntryTime,
            exitTime = reservationExitTime,
            totalCost = totalCost
        )
    )
    val uiState: StateFlow<ConfirmUiState> = _uiState.asStateFlow()

    init {
        loadDetail()
    }

    fun loadDetail() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = getReservationDetailUseCase(parkingId)
            result.fold(
                onSuccess = { detail ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        detail = detail
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "No se pudo obtener el parqueo"
                    )
                }
            )
        }
    }

    fun confirmReservation() {
        val detail = _uiState.value.detail ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isConfirming = true, errorMessage = null)

            val request = ReservationRequestModel(
                userId = userId,
                parking = detail.parking,
                date = reservationDate,
                entryTime = reservationEntryTime,
                exitTime = reservationExitTime,
                totalCost = totalCost
            )

            confirmReservationUseCase(request).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isConfirming = false,
                        success = true
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isConfirming = false,
                        errorMessage = error.message ?: "No se pudo confirmar la reserva"
                    )
                }
            )
        }
    }

    fun consumeSuccess() {
        _uiState.value = _uiState.value.copy(success = false)
    }
}
