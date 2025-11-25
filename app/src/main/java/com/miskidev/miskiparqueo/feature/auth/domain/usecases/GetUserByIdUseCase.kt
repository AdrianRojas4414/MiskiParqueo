package com.miskidev.miskiparqueo.feature.auth.domain.usecases

import com.miskidev.miskiparqueo.feature.auth.login.domain.repository.IAuthRepository

class GetUserByIdUseCase(private val repository: IAuthRepository) {
    suspend operator fun invoke(userId: String) = repository.getUserById(userId)
}