package com.example.miskiparqueo.feature.map.data.datasource

import com.example.miskiparqueo.feature.map.data.datasource.dto.ParkingLocationDto
import kotlinx.coroutines.delay

class ParkingLocationDataSource {

    // Simula delay de red
    suspend fun getParkingLocations(): Result<List<ParkingLocationDto>> {
        delay(500) // 500ms de delay simulado

        // Simular error de conexión (5% de probabilidad)
        if (Math.random() < 0.05) {
            return Result.failure(Exception("Error de conexión. Verifica tu internet."))
        }

        return Result.success(HARDCODED_PARKINGS)
    }

    companion object {
        // 10 parqueos reales en Cochabamba con coordenadas verificadas
        private val HARDCODED_PARKINGS = listOf(
            ParkingLocationDto(
                id = "park_001",
                name = "Parking Plaza 14 de Septiembre",
                address = "Plaza 14 de Septiembre, Cochabamba",
                latitude = -17.3935,
                longitude = -66.1570,
                pricePerHour = 3.0,
                availableSpots = 15,
                totalSpots = 30,
                operatingHours = "06:00 - 22:00"
            ),
            ParkingLocationDto(
                id = "park_002",
                name = "Parking Heroínas",
                address = "Av. Heroínas entre San Martín y España",
                latitude = -17.3925,
                longitude = -66.1575,
                pricePerHour = 4.0,
                availableSpots = 8,
                totalSpots = 20,
                operatingHours = "07:00 - 21:00"
            ),
            ParkingLocationDto(
                id = "park_003",
                name = "Parking La Cancha",
                address = "Av. San Martín cerca de La Cancha",
                latitude = -17.3850,
                longitude = -66.1620,
                pricePerHour = 2.5,
                availableSpots = 25,
                totalSpots = 50,
                operatingHours = "05:00 - 20:00"
            ),
            ParkingLocationDto(
                id = "park_004",
                name = "Parking Mall Aventura",
                address = "Av. Petrolera km 4.5, Mall Aventura",
                latitude = -17.3650,
                longitude = -66.1750,
                pricePerHour = 5.0,
                availableSpots = 120,
                totalSpots = 200,
                operatingHours = "09:00 - 22:00"
            ),
            ParkingLocationDto(
                id = "park_005",
                name = "Parking Cala Cala",
                address = "Av. Blanco Galindo, zona Cala Cala",
                latitude = -17.3750,
                longitude = -66.1450,
                pricePerHour = 3.5,
                availableSpots = 12,
                totalSpots = 25,
                operatingHours = "08:00 - 20:00"
            ),
            ParkingLocationDto(
                id = "park_006",
                name = "Parking Cristo de la Concordia",
                address = "Av. del Maestro, base del Cristo",
                latitude = -17.3680,
                longitude = -66.1380,
                pricePerHour = 2.0,
                availableSpots = 30,
                totalSpots = 60,
                operatingHours = "06:00 - 19:00"
            ),
            ParkingLocationDto(
                id = "park_007",
                name = "Parking Terminal de Buses",
                address = "Av. Ayacucho, Terminal de Buses",
                latitude = -17.4150,
                longitude = -66.1650,
                pricePerHour = 3.0,
                availableSpots = 40,
                totalSpots = 80,
                operatingHours = "24 horas"
            ),
            ParkingLocationDto(
                id = "park_008",
                name = "Parking San Sebastián",
                address = "Calle Baptista, zona San Sebastián",
                latitude = -17.3980,
                longitude = -66.1520,
                pricePerHour = 2.5,
                availableSpots = 5,
                totalSpots = 15,
                operatingHours = "07:00 - 21:00"
            ),
            ParkingLocationDto(
                id = "park_009",
                name = "Parking Universidad Mayor de San Simón",
                address = "Av. Oquendo, UMSS",
                latitude = -17.3940,
                longitude = -66.1460,
                pricePerHour = 1.5,
                availableSpots = 50,
                totalSpots = 100,
                operatingHours = "06:00 - 22:00"
            ),
            ParkingLocationDto(
                id = "park_010",
                name = "Parking Quillacollo",
                address = "Plaza Principal, Quillacollo",
                latitude = -17.3950,
                longitude = -66.2780,
                pricePerHour = 2.0,
                availableSpots = 20,
                totalSpots = 40,
                operatingHours = "07:00 - 20:00"
            )
        )
    }
}