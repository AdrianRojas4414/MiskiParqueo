package com.example.miskiparqueo.feature.auth.login.data.datasource

import kotlinx.coroutines.delay
import java.util.UUID

class LoginRemoteDataSource {
    private val users = listOf(
        UserDto(
            userId = UUID.randomUUID().toString(),
            firstName = "Admin",
            lastName = "User",
            username = "admin",
            email = "admin@test.com",
            password = "password123"
        ),
        UserDto(
            userId = UUID.randomUUID().toString(),
            firstName = "Juan",
            lastName = "Perez",
            username = "juan123",
            email = "juan@mail.com",
            password = "password123"
        ),
        UserDto(
            userId = UUID.randomUUID().toString(),
            firstName = "Test",
            lastName = "Account",
            username = "test_user",
            email = "test@example.com",
            password = "securepass"
        )
    )

    /**
     * Simula la autenticación de un usuario.
     * @param credential puede ser un email o un username.
     * @param password la contraseña en texto plano.
     * @return Result con LoginResponse en caso de éxito o una excepción en caso de fallo.
     */
    suspend fun login(credential: String, password: String): Result<LoginResponse> {
        delay(500) // Simular latencia de red

        // Simular un error de conexión aleatorio
        if (Math.random() < 0.1) {
            return Result.failure(Exception("Error de conexión. Verifica tu internet."))
        }

        val normalizedCredential = credential.trim().lowercase()

        val user = users.find {
            it.username.lowercase() == normalizedCredential || it.email.lowercase() == normalizedCredential
        }

        return when {
            user == null -> {
                Result.failure(Exception("Usuario no encontrado."))
            }
            user.password != password -> {
                Result.failure(Exception("Contraseña incorrecta."))
            }
            else -> {
                Result.success(
                    LoginResponse(
                        userId = user.userId,
                        firstName = user.firstName,
                        lastName = user.lastName,
                        username = user.username,
                        email = user.email
                    )
                )
            }
        }
    }
}

// DTO (Data Transfer Object) para representar al usuario en la capa de datos.
data class UserDto(
    val userId: String,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val password: String // Solo para la simulación
)

// Clase para la respuesta del DataSource.
data class LoginResponse(
    val userId: String,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String
)