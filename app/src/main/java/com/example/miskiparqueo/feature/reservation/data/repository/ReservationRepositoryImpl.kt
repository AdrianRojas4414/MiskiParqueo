package com.example.miskiparqueo.feature.reservation.data.repository

import com.example.miskiparqueo.feature.map.domain.repository.IParkingRepository
import com.example.miskiparqueo.feature.reservation.data.datasource.ParkingExtrasDataSource
import com.example.miskiparqueo.feature.reservation.data.datasource.ReservationFirebaseDataSource
import com.example.miskiparqueo.feature.reservation.data.datasource.dto.ReservationRecordDto
import com.example.miskiparqueo.feature.reservation.domain.model.ParkingReservationDetailModel
import com.example.miskiparqueo.feature.reservation.domain.model.ReservationRecordModel
import com.example.miskiparqueo.feature.reservation.domain.model.ReservationRequestModel
import com.example.miskiparqueo.feature.reservation.domain.model.ReservationStatus
import com.example.miskiparqueo.feature.reservation.domain.repository.IReservationRepository
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReservationRepositoryImpl(
    private val parkingRepository: IParkingRepository,
    private val parkingExtrasDataSource: ParkingExtrasDataSource,
    private val reservationFirebaseDataSource: ReservationFirebaseDataSource
) : IReservationRepository {

    override suspend fun getReservationDetail(parkingId: String): Result<ParkingReservationDetailModel> {
        val parkingResult = parkingRepository.getParkingById(parkingId)
        val extrasResult = parkingExtrasDataSource.getParkingExtras(parkingId)

        return parkingResult.fold(
            onSuccess = { parking ->
                extrasResult.fold(
                    onSuccess = { extras ->
                        Result.success(
                            ParkingReservationDetailModel(
                                parking = parking,
                                imageName = extras.imageName,
                                amenities = extras.amenities,
                                description = extras.description
                            )
                        )
                    },
                    onFailure = { Result.failure(it) }
                )
            },
            onFailure = { Result.failure(it) }
        )
    }

    override suspend fun confirmReservation(request: ReservationRequestModel): Result<Unit> {
        return try {
            val dto = ReservationRecordDto(
                id = "", // Se generará en el DataSource
                userId = request.userId,
                parkingId = request.parking.id,
                parkingName = request.parking.name,
                parkingAddress = request.parking.address,
                date = request.date.toString(),
                entryTime = request.entryTime.toString(),
                exitTime = request.exitTime.toString(),
                totalCost = request.totalCost,
                status = ReservationStatus.ACTIVE.name,
                createdAt = System.currentTimeMillis()
            )

            // Guardar en Firebase
            val result = reservationFirebaseDataSource.saveReservation(dto)

            result.fold(
                onSuccess = {
                    println("✅ Reserva confirmada y guardada en Firebase")
                    Result.success(Unit)
                },
                onFailure = { error ->
                    println("❌ Error al guardar reserva: ${error.message}")
                    Result.failure(error)
                }
            )
        } catch (e: Exception) {
            println("❌ Excepción al confirmar reserva: ${e.message}")
            Result.failure(e)
        }
    }

    override fun observeActiveReservations(userId: String): Flow<List<ReservationRecordModel>> {
        // Ahora observa solo las reservas del usuario específico
        return reservationFirebaseDataSource.observeUserReservations(userId).map { records ->
            records
                .filter { it.status == ReservationStatus.ACTIVE.name }
                .map { dto ->
                    ReservationRecordModel(
                        id = dto.id,
                        userId = dto.userId,
                        parkingId = dto.parkingId,
                        parkingName = dto.parkingName,
                        parkingAddress = dto.parkingAddress,
                        date = LocalDate.parse(dto.date),
                        entryTime = LocalTime.parse(dto.entryTime),
                        exitTime = LocalTime.parse(dto.exitTime),
                        totalCost = dto.totalCost,
                        status = ReservationStatus.valueOf(dto.status),
                        createdAt = dto.createdAt
                    )
                }
        }
    }
}
