package com.example.miskiparqueo.feature.reservation.data.datasource

import com.example.miskiparqueo.feature.reservation.data.datasource.dto.ParkingExtrasDto

class ParkingExtrasDataSource {

    // TODO: Reemplazar por una fuente real (BD o API) cuando est\u00e9 disponible.
    suspend fun getParkingExtras(parkingId: String): Result<ParkingExtrasDto> {
        val extras = PARKING_EXTRAS[parkingId] ?: DEFAULT_EXTRAS
        return Result.success(extras)
    }

    companion object {
        private val DEFAULT_EXTRAS = ParkingExtrasDto(
            imageUrl = "https://images.unsplash.com/photo-1503736334956-4c8f8e92946d?auto=format&fit=crop&w=1200&q=80",
            amenities = listOf("Seguridad 24/7", "C\u00e1maras CCTV", "Pago con QR"),
            description = "Parqueo vigilado y f\u00e1cil acceso al centro."
        )

        private val PARKING_EXTRAS = mapOf(
            "park_001" to ParkingExtrasDto(
                imageUrl = "https://images.unsplash.com/photo-1503736334956-4c8f8e92946d?auto=format&fit=crop&w=1200&q=80",
                amenities = listOf("Seguridad 24/7", "C\u00e1maras CCTV", "Ba\u00f1os", "Pago con QR"),
                description = "Ubicado frente a la plaza principal con vigilancia constante."
            ),
            "park_002" to ParkingExtrasDto(
                imageUrl = "https://images.unsplash.com/photo-1493238792000-8113da705763?auto=format&fit=crop&w=1200&q=80",
                amenities = listOf("Techo cubierto", "Lavado express", "Ba\u00f1os"),
                description = "Ideal para compras r\u00e1pidas en la zona de las Hero\u00ednas."
            ),
            "park_003" to ParkingExtrasDto(
                imageUrl = "https://images.unsplash.com/photo-1449960238630-f09530bf5b9a?auto=format&fit=crop&w=1200&q=80",
                amenities = listOf("Amplios espacios", "Iluminaci\u00f3n LED", "Personal en sitio"),
                description = "Espacios amplios para camionetas a pocos pasos de La Cancha."
            ),
            "park_004" to ParkingExtrasDto(
                imageUrl = "https://images.unsplash.com/photo-1489515217757-5fd1be406fef?auto=format&fit=crop&w=1200&q=80",
                amenities = listOf("Conexi\u00f3n directa al mall", "Cargadores EV", "Espacios premium"),
                description = "Dentro del Mall Aventura con acceso techado."
            ),
            "park_005" to ParkingExtrasDto(
                imageUrl = "https://images.unsplash.com/photo-1502877338535-766e1452684a?auto=format&fit=crop&w=1200&q=80",
                amenities = listOf("Seguridad privada", "Ba\u00f1os", "Estacionamiento cubierto"),
                description = "Parqueo acogedor en la zona Cala Cala."
            ),
            "park_006" to ParkingExtrasDto(
                imageUrl = "https://images.unsplash.com/photo-1503376780353-7e6692767b70?auto=format&fit=crop&w=1200&q=80",
                amenities = listOf("Vista panor\u00e1mica", "Pago con tarjeta", "Cafeter\u00eda cercana"),
                description = "Excelente opci\u00f3n al visitar el Cristo de la Concordia."
            ),
            "park_007" to ParkingExtrasDto(
                imageUrl = "https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1200&q=80",
                amenities = listOf("24 horas", "Iluminaci\u00f3n LED", "Ba\u00f1os"),
                description = "Operativo todo el d\u00eda junto a la terminal."
            ),
            "park_008" to ParkingExtrasDto(
                imageUrl = "https://images.unsplash.com/photo-1529429617124-aee711a70426?auto=format&fit=crop&w=1200&q=80",
                amenities = listOf("Cobertura techada", "Vigilancia privada", "Pago con QR"),
                description = "Ideal para visitas al mercado San Sebasti\u00e1n."
            ),
            "park_009" to ParkingExtrasDto(
                imageUrl = "https://images.unsplash.com/photo-1504215680853-026ed2a45def?auto=format&fit=crop&w=1200&q=80",
                amenities = listOf("Descuentos para estudiantes", "Bicicletero", "Camaras"),
                description = "Preferido por universitarios de la UMSS."
            ),
            "park_010" to ParkingExtrasDto(
                imageUrl = "https://images.unsplash.com/photo-1529927066849-6f10a19b03b1?auto=format&fit=crop&w=1200&q=80",
                amenities = listOf("Seguridad 24/7", "Espacios amplios", "Pago con tarjeta"),
                description = "El parqueo central de Quillacollo."
            )
        )
    }
}
