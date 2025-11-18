package com.example.miskiparqueo.feature.reservation.data.datasource

import com.example.miskiparqueo.feature.reservation.data.datasource.dto.ReservationFirebaseDto
import com.example.miskiparqueo.feature.reservation.data.datasource.dto.ReservationRecordDto
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.UUID
import kotlin.coroutines.resume

class ReservationFirebaseDataSource {
    private val database = FirebaseDatabase.getInstance()
    private val reservationsRef = database.getReference("reservations")

    /**
     * Observa las reservaciones de un usuario específico en tiempo real desde Firebase
     * @param userId ID del usuario del cual se quieren obtener las reservas
     */
    fun observeUserReservations(userId: String): Flow<List<ReservationRecordDto>> = callbackFlow {
        // Referencia específica al nodo del usuario
        val userReservationsRef = reservationsRef.child(userId)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val reservations = mutableListOf<ReservationRecordDto>()

                for (reservationSnapshot in snapshot.children) {
                    try {
                        val firebaseDto = reservationSnapshot.getValue(ReservationFirebaseDto::class.java)

                        if (firebaseDto != null) {
                            val recordDto = ReservationRecordDto(
                                id = firebaseDto.id,
                                userId = firebaseDto.userId,
                                parkingId = firebaseDto.parkingId,
                                parkingName = firebaseDto.parkingName,
                                parkingAddress = firebaseDto.parkingAddress,
                                date = firebaseDto.date,
                                entryTime = firebaseDto.entryTime,
                                exitTime = firebaseDto.exitTime,
                                totalCost = firebaseDto.totalCost,
                                status = firebaseDto.status,
                                createdAt = firebaseDto.createdAt
                            )
                            reservations.add(recordDto)
                        }
                    } catch (e: Exception) {
                        println("❌ Error parsing reservation: ${e.message}")
                    }
                }

                // Ordenar por fecha de creación (más reciente primero)
                val sortedReservations = reservations.sortedByDescending { it.createdAt }
                trySend(sortedReservations)
            }

            override fun onCancelled(error: DatabaseError) {
                println("❌ Firebase error: ${error.message}")
                close(error.toException())
            }
        }

        userReservationsRef.addValueEventListener(listener)

        awaitClose {
            userReservationsRef.removeEventListener(listener)
        }
    }

    /**
     * Guarda una nueva reservación en Firebase bajo el nodo del usuario
     * @param dto Datos de la reservación a guardar
     */
    suspend fun saveReservation(dto: ReservationRecordDto): Result<ReservationRecordDto> =
        suspendCancellableCoroutine { continuation ->
            val reservationId = if (dto.id.isEmpty()) {
                UUID.randomUUID().toString()
            } else dto.id

            val recordWithId = dto.copy(id = reservationId)

            val firebaseDto = ReservationFirebaseDto(
                id = recordWithId.id,
                userId = recordWithId.userId,
                parkingId = recordWithId.parkingId,
                parkingName = recordWithId.parkingName,
                parkingAddress = recordWithId.parkingAddress,
                date = recordWithId.date,
                entryTime = recordWithId.entryTime,
                exitTime = recordWithId.exitTime,
                totalCost = recordWithId.totalCost,
                status = recordWithId.status,
                createdAt = recordWithId.createdAt
            )

            // Guardar bajo: reservations/{userId}/{reservationId}
            reservationsRef
                .child(recordWithId.userId)
                .child(reservationId)
                .setValue(firebaseDto)
                .addOnSuccessListener {
                    println("✅ Reserva guardada exitosamente: $reservationId")
                    continuation.resume(Result.success(recordWithId))
                }
                .addOnFailureListener { error ->
                    println("❌ Error guardando reserva: ${error.message}")
                    continuation.resume(Result.failure(error))
                }
        }

    /**
     * Obtiene una reservación específica
     * @param userId ID del usuario
     * @param reservationId ID de la reservación
     */
    suspend fun getReservation(userId: String, reservationId: String): Result<ReservationRecordDto> =
        suspendCancellableCoroutine { continuation ->
            reservationsRef
                .child(userId)
                .child(reservationId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        try {
                            val firebaseDto = snapshot.getValue(ReservationFirebaseDto::class.java)

                            if (firebaseDto != null) {
                                val recordDto = ReservationRecordDto(
                                    id = firebaseDto.id,
                                    userId = firebaseDto.userId,
                                    parkingId = firebaseDto.parkingId,
                                    parkingName = firebaseDto.parkingName,
                                    parkingAddress = firebaseDto.parkingAddress,
                                    date = firebaseDto.date,
                                    entryTime = firebaseDto.entryTime,
                                    exitTime = firebaseDto.exitTime,
                                    totalCost = firebaseDto.totalCost,
                                    status = firebaseDto.status,
                                    createdAt = firebaseDto.createdAt
                                )
                                continuation.resume(Result.success(recordDto))
                            } else {
                                continuation.resume(Result.failure(Exception("Reserva no encontrada")))
                            }
                        } catch (e: Exception) {
                            continuation.resume(Result.failure(e))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        continuation.resume(Result.failure(error.toException()))
                    }
                })
        }
}