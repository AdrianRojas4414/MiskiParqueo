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
     * Observa las reservaciones en tiempo real desde Firebase
     */
    fun observeReservations(): Flow<List<ReservationRecordDto>> = callbackFlow {
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
                        println("Error parsing reservation: ${e.message}")
                    }
                }

                trySend(reservations)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Firebase error: ${error.message}")
                close(error.toException())
            }
        }

        reservationsRef.addValueEventListener(listener)

        awaitClose {
            reservationsRef.removeEventListener(listener)
        }
    }

    /**
     * Guarda una nueva reservación en Firebase
     */
    suspend fun saveReservation(dto: ReservationRecordDto): ReservationRecordDto =
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

            reservationsRef.child(reservationId).setValue(firebaseDto)
                .addOnSuccessListener {
                    continuation.resume(recordWithId)
                }
                .addOnFailureListener { error ->
                    continuation.resume(recordWithId) // En caso de error, devolvemos el DTO
                    println("Error saving reservation: ${error.message}")
                }
        }
}