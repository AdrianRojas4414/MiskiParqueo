package com.example.miskiparqueo.feature.reservation.data.datasource

import com.example.miskiparqueo.feature.reservation.data.datasource.dto.ReservationRecordDto
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ReservationLocalDataSource {

    private val mutex = Mutex()
    private val reservationsFlow = MutableStateFlow<List<ReservationRecordDto>>(emptyList())

    fun observeReservations(): StateFlow<List<ReservationRecordDto>> = reservationsFlow

    suspend fun saveReservation(dto: ReservationRecordDto): ReservationRecordDto {
        val recordWithId = if (dto.id.isEmpty()) {
            dto.copy(id = UUID.randomUUID().toString())
        } else dto

        mutex.withLock {
            reservationsFlow.value = reservationsFlow.value + recordWithId
        }
        return recordWithId
    }
}
