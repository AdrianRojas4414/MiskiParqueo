package com.miskidev.miskiparqueo.feature.reservation.data.datasource

import com.miskidev.miskiparqueo.feature.map.data.datasource.dto.ParkingFirebaseDto
import com.miskidev.miskiparqueo.feature.reservation.data.datasource.dto.ParkingExtrasDto
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class ParkingExtrasDataSource {
    private val database = FirebaseDatabase.getInstance()
    private val parkingsRef = database.getReference("parkings")

    suspend fun getParkingExtras(parkingId: String): Result<ParkingExtrasDto> =
        suspendCancellableCoroutine { continuation ->
            parkingsRef.child(parkingId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val firebaseDto = snapshot.getValue(ParkingFirebaseDto::class.java)

                        if (firebaseDto != null) {
                            val extrasDto = ParkingExtrasDto(
                                imageName = firebaseDto.imageName.ifEmpty { "img_parking_default" },
                                amenities = firebaseDto.amenities,
                                description = firebaseDto.description
                            )
                            continuation.resume(Result.success(extrasDto))
                        } else {
                            // Retornar datos por defecto si no se encuentra
                            continuation.resume(Result.success(DEFAULT_EXTRAS))
                        }
                    } catch (e: Exception) {
                        continuation.resume(Result.failure(e))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resume(
                        Result.failure(Exception("Error al obtener extras: ${error.message}"))
                    )
                }
            })
        }

    companion object {
        private val DEFAULT_EXTRAS = ParkingExtrasDto(
            imageName = "img_parking_default",
            amenities = listOf("Seguridad 24/7", "Cámaras CCTV", "Pago con QR"),
            description = "Parqueo vigilado y fácil acceso al centro."
        )
    }
}
