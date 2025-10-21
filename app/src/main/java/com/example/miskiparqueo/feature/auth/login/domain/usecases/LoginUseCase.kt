package com.example.miskiparqueo.feature.auth.login.domain.usecases

import com.example.miskiparqueo.feature.auth.domain.model.UserModel
import com.example.miskiparqueo.feature.auth.login.domain.repository.IAuthRepository
import com.example.miskiparqueo.feature.auth.signup.domain.model.vo.Password

class LoginUseCase(
    private val repository: IAuthRepository
) {
    suspend operator fun invoke(credential: String, password: Password): Result<UserModel> {
        if (credential.isBlank()) {
            return Result.failure(IllegalArgumentException("El email o username no puede estar vacío"))
        }
        return repository.login(credential, password)
    }
}