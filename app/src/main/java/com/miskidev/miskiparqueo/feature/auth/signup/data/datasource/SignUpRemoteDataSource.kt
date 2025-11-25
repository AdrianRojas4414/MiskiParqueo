package com.miskidev.miskiparqueo.feature.auth.signup.data.datasource

import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.Email
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.Username
import kotlinx.coroutines.delay
import java.util.UUID

class SignUpRemoteDataSource {
    private val registeredUsernames = mutableSetOf("admin", "test_user", "juan123")
    private val registeredEmails = mutableSetOf("admin@test.com", "test@example.com", "juan@mail.com")

    /**
     * Simula el registro de un usuario
     * @return Pair<Boolean, String> - (éxito, userId o mensaje de error)
     */
    suspend fun signUp(
        firstName: String,
        lastName: String,
        username: Username,
        email: Email,
        password: String
    ): Result<SignUpResponse> {
        delay(500)

        if (Math.random() < 0.1) {
            return Result.failure(Exception("Error de conexión. Verifica tu internet."))
        }

        if (registeredUsernames.contains(username.value.lowercase())) {
            return Result.failure(Exception("El username ya está en uso"))
        }

        if (registeredEmails.contains(email.value.lowercase())) {
            return Result.failure(Exception("El email ya está registrado"))
        }

        val userId = UUID.randomUUID().toString()

        registeredUsernames.add(username.value.lowercase())
        registeredEmails.add(email.value.lowercase())

        return Result.success(
            SignUpResponse(
                userId = userId,
                firstName = firstName,
                lastName = lastName,
                username = username.value,
                email = email.value
            )
        )
    }

    data class SignUpResponse(
        val userId: String,
        val firstName: String,
        val lastName: String,
        val username: String,
        val email: String
    )
}