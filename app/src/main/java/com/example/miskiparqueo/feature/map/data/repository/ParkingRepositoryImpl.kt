package com.example.miskiparqueo.feature.map.data.repository

import com.example.miskiparqueo.feature.map.data.datasource.ParkingLocationDataSource
import com.example.miskiparqueo.feature.map.domain.model.ParkingLocationModel
import com.example.miskiparqueo.feature.map.domain.repository.IParkingRepository

/**
 * Implementación concreta del [IParkingRepository].
 * Esta clase sabe *cómo* obtener los datos (a través del DataSource)
 * y *cómo* mapearlos al modelo de dominio.
 */
class ParkingRepositoryImpl(
    private val dataSource: ParkingLocationDataSource
) : IParkingRepository {

    override suspend fun getParkingLocations(): Result<List<ParkingLocationModel>> {
        return try {
            // Llama a la fuente de datos
            val result = dataSource.getParkingLocations()

            // Mapea el resultado
            result.map { dtoList ->
                // Convierte la lista de DTOs a una lista de Entidades de Dominio
                dtoList.map { dto ->
                    ParkingLocationModel(
                        id = dto.id,
                        name = dto.name,
                        latitude = dto.latitude,
                        longitude = dto.longitude
                    )
                }
            }
        } catch (e: Exception) {
            // Captura cualquier excepción inesperada
            Result.failure(e)
        }
    }
}