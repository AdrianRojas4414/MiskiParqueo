package com.example.miskiparqueo.feature.auth.domain.usecases

import com.example.miskiparqueo.feature.auth.login.domain.repository.IAuthRepository

class GetUserByIdUseCase(private val repository: IAuthRepository) {
    suspend operator fun invoke(userId: String) = repository.getUserById(userId)
}