package com.example.miskiparqueo.feature.reservation.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miskiparqueo.feature.reservation.domain.model.ReservationRecordModel
import com.example.miskiparqueo.feature.reservation.domain.usecases.ObserveActiveReservationsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ReservationListViewModel(
    userId: String,
    observeActiveReservationsUseCase: ObserveActiveReservationsUseCase
) : ViewModel() {

    val reservations: StateFlow<List<ReservationRecordModel>> =
        observeActiveReservationsUseCase(userId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
}
