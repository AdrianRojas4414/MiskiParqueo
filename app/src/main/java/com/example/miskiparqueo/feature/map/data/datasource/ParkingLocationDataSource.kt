package com.example.miskiparqueo.feature.map.data.datasource

import com.example.miskiparqueo.feature.map.data.datasource.dto.ParkingLocationDto
import kotlinx.coroutines.delay
import java.util.UUID

/**
 * Simula una fuente de datos remota (API) para las ubicaciones de los parqueos.
 * En una app real, aquí se harían las llamadas a Retrofit, Firebase, etc.
 */
class ParkingLocationDataSource {

    // Lista hardcodeada de parqueos en Cochabamba
    private val fakeParkingDb = listOf(
        ParkingLocationDto(
            id = UUID.randomUUID().toString(),
            name = "Parqueo Diego",
            latitude = -17.22346,
            longitude = -661126.7
        ),
        ParkingLocationDto(
            id = UUID.randomUUID().toString(),
            name = "Parqueo Adrian",
            latitude = -17.22463,
            longitude = -66.11173
        ),
        ParkingLocationDto(
            id = UUID.randomUUID().toString(),
            name = "Parqueo André",
            latitude = -17.22220,
            longitude = -66.11020
        ),
    )

    /**
     * Simula la obtención de la lista de parqueos desde un servidor.
     * @return Un Result con la lista de DTOs.
     */
    suspend fun getParkingLocations(): Result<List<ParkingLocationDto>> {
        delay(1000) // Simula 1 segundo de latencia de red

        // Simular un posible error de red
        if (Math.random() < 0.1) {
            return Result.failure(Exception("Error de conexión al cargar parqueos."))
        }

        // Éxito
        return Result.success(fakeParkingDb)
    }
}