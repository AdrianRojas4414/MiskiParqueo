package com.miskidev.miskiparqueo.feature.reservation.data.repository

import com.miskidev.miskiparqueo.feature.map.domain.repository.IParkingRepository
import com.miskidev.miskiparqueo.feature.reservation.data.datasource.ParkingExtrasDataSource
import com.miskidev.miskiparqueo.feature.reservation.data.datasource.ReservationFirebaseDataSource
import com.miskidev.miskiparqueo.feature.reservation.data.datasource.dto.ReservationRecordDto
import com.miskidev.miskiparqueo.feature.reservation.domain.model.ParkingReservationDetailModel
import com.miskidev.miskiparqueo.feature.reservation.domain.model.ReservationRecordModel
import com.miskidev.miskiparqueo.feature.reservation.domain.model.ReservationRequestModel
import com.miskidev.miskiparqueo.feature.reservation.domain.model.ReservationStatus
import com.miskidev.miskiparqueo.feature.reservation.domain.repository.IReservationRepository
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class ReservationRepositoryImpl(
    private val parkingRepository: IParkingRepository,
    private val parkingExtrasDataSource: ParkingExtrasDataSource,
    private val reservationFirebaseDataSource: ReservationFirebaseDataSource
) : IReservationRepository {

    private val database = FirebaseDatabase.getInstance()
    private val parkingsRef = database.getReference("parkings")

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
            // 1. Verificar cupos disponibles
            val parkingSnapshot = parkingsRef.child(request.parking.id).get().await()
            val currentAvailableSpots = parkingSnapshot.child("availableSpots").getValue(Int::class.java) ?: 0

            if (currentAvailableSpots <= 0) {
                return Result.failure(Exception("No hay cupos disponibles en este parqueo"))
            }

            // 2. Crear el DTO de la reserva
            val dto = ReservationRecordDto(
                id = "",
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

            // 3. Guardar la reserva en Firebase
            val reservationResult = reservationFirebaseDataSource.saveReservation(dto)

            reservationResult.fold(
                onSuccess = {
                    // 4. Actualizar los cupos disponibles (restar 1)
                    val newAvailableSpots = currentAvailableSpots - 1
                    parkingsRef.child(request.parking.id)
                        .child("availableSpots")
                        .setValue(newAvailableSpots)
                        .await()

                    println("✅ Reserva confirmada y cupos actualizados: $newAvailableSpots/${request.parking.totalSpots}")
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