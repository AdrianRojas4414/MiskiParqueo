package com.miskidev.miskiparqueo.feature.map.data.datasource

import com.miskidev.miskiparqueo.feature.map.data.datasource.dto.ParkingFirebaseDto
import com.miskidev.miskiparqueo.feature.map.data.datasource.dto.ParkingLocationDto
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class ParkingLocationDataSource {
    private val database = FirebaseDatabase.getInstance()
    private val parkingsRef = database.getReference("parkings")

    suspend fun getParkingLocations(): Result<List<ParkingLocationDto>> =
        suspendCancellableCoroutine { continuation ->
            parkingsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val parkings = mutableListOf<ParkingLocationDto>()

                    for (parkingSnapshot in snapshot.children) {
                        try {
                            val firebaseDto = parkingSnapshot.getValue(ParkingFirebaseDto::class.java)

                            if (firebaseDto != null) {
                                // Convertir de Firebase DTO a Location DTO
                                val locationDto = ParkingLocationDto(
                                    id = firebaseDto.id,
                                    name = firebaseDto.name,
                                    address = firebaseDto.address,
                                    latitude = firebaseDto.latitude,
                                    longitude = firebaseDto.longitude,
                                    pricePerHour = firebaseDto.pricePerHour,
                                    availableSpots = firebaseDto.availableSpots,
                                    totalSpots = firebaseDto.totalSpots,
                                    operatingHours = firebaseDto.operatingHours
                                )
                                parkings.add(locationDto)
                            }
                        } catch (e: Exception) {
                            // Log error pero continúa con otros parqueos
                            println("Error parsing parking: ${e.message}")
                        }
                    }

                    if (parkings.isNotEmpty()) {
                        continuation.resume(Result.success(parkings))
                    } else {
                        continuation.resume(Result.failure(Exception("No se encontraron parqueos")))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resume(
                        Result.failure(Exception("Error de conexión: ${error.message}"))
                    )
                }
            })
        }
}