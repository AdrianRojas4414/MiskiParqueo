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
            imageName = "img_parking_default",
            amenities = listOf("Seguridad 24/7", "C\u00e1maras CCTV", "Pago con QR"),
            description = "Parqueo vigilado y f\u00e1cil acceso al centro."
        )

        // Para usar tus propias fotos:
        // 1. Copia tus imágenes (PNG/JPG/WebP) dentro de app/src/main/res/drawable.
        // 2. Usa el nombre del archivo sin extensión en imageName (ej. "parqueo_centro").
        private val PARKING_EXTRAS = mapOf(
            "park_001" to ParkingExtrasDto(
                imageName = "img_parking_default",
                amenities = listOf("Seguridad 24/7", "C\u00e1maras CCTV", "Ba\u00f1os", "Pago con QR"),
                description = "Ubicado frente a la plaza principal con vigilancia constante."
            ),
            "park_002" to ParkingExtrasDto(
                imageName = "img_parking_default",
                amenities = listOf("Techo cubierto", "Lavado express", "Ba\u00f1os"),
                description = "Ideal para compras r\u00e1pidas en la zona de las Hero\u00ednas."
            ),
            "park_003" to ParkingExtrasDto(
                imageName = "img_parking_default",
                amenities = listOf("Amplios espacios", "Iluminaci\u00f3n LED", "Personal en sitio"),
                description = "Espacios amplios para camionetas a pocos pasos de La Cancha."
            ),
            "park_004" to ParkingExtrasDto(
                imageName = "img_parking_default",
                amenities = listOf("Conexi\u00f3n directa al mall", "Cargadores EV", "Espacios premium"),
                description = "Dentro del Mall Aventura con acceso techado."
            ),
            "park_005" to ParkingExtrasDto(
                imageName = "img_parking_default",
                amenities = listOf("Seguridad privada", "Ba\u00f1os", "Estacionamiento cubierto"),
                description = "Parqueo acogedor en la zona Cala Cala."
            ),
            "park_006" to ParkingExtrasDto(
                imageName = "img_parking_default",
                amenities = listOf("Vista panor\u00e1mica", "Pago con tarjeta", "Cafeter\u00eda cercana"),
                description = "Excelente opci\u00f3n al visitar el Cristo de la Concordia."
            ),
            "park_007" to ParkingExtrasDto(
                imageName = "img_parking_default",
                amenities = listOf("24 horas", "Iluminaci\u00f3n LED", "Ba\u00f1os"),
                description = "Operativo todo el d\u00eda junto a la terminal."
            ),
            "park_008" to ParkingExtrasDto(
                imageName = "img_parking_default",
                amenities = listOf("Cobertura techada", "Vigilancia privada", "Pago con QR"),
                description = "Ideal para visitas al mercado San Sebasti\u00e1n."
            ),
            "park_009" to ParkingExtrasDto(
                imageName = "img_parking_default",
                amenities = listOf("Descuentos para estudiantes", "Bicicletero", "Camaras"),
                description = "Preferido por universitarios de la UMSS."
            ),
            "park_010" to ParkingExtrasDto(
                imageName = "img_parking_default",
                amenities = listOf("Seguridad 24/7", "Espacios amplios", "Pago con tarjeta"),
                description = "El parqueo central de Quillacollo."
            )
        )
    }
}
