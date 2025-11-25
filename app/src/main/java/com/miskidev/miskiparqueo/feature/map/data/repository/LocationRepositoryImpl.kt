package com.miskidev.miskiparqueo.feature.map.data.repository

import android.annotation.SuppressLint
import android.content.Context
import com.miskidev.miskiparqueo.feature.map.domain.repository.ILocationRepository
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Implementación concreta de [ILocationRepository] que usa el
 * FusedLocationProviderClient de Google Play Services.
 */
class LocationRepositoryImpl(
    private val context: Context
) : ILocationRepository {

    // Cliente principal para interactuar con el proveedor de ubicación.
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission") // Los permisos se manejarán en la UI (MapScreen)
    override suspend fun getCurrentLocation(): Result<LatLng> {

        // Usamos esta corrutina especial para adaptar la API de Callbacks de Google
        return suspendCancellableCoroutine { continuation ->

            // Creamos un token de cancelación
            val cancellationTokenSource = CancellationTokenSource()

            // Solicitamos la ubicación actual con alta precisión
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { location ->
                if (location != null) {
                    // Éxito: Devolvemos la LatLng
                    continuation.resume(Result.success(LatLng(location.latitude, location.longitude)))
                } else {
                    // Éxito pero la ubicación es nula (puede pasar)
                    continuation.resume(Result.failure(Exception("No se pudo obtener la ubicación.")))
                }
            }.addOnFailureListener { exception ->
                // Fallo: Devolvemos la excepción
                continuation.resume(Result.failure(exception))
            }

            // Si la corrutina se cancela, cancelamos la solicitud de ubicación
            continuation.invokeOnCancellation {
                cancellationTokenSource.cancel()
            }
        }
    }
}