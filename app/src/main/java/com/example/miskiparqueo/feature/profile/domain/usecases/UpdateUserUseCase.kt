package com.example.miskiparqueo.feature.profile.domain.usecases

import com.example.miskiparqueo.feature.auth.domain.model.UserModel
import com.example.miskiparqueo.feature.profile.domain.repository.IProfileRepository

class UpdateUserUseCase(private val repository: IProfileRepository) {
    suspend operator fun invoke(user: UserModel) = repository.updateUser(user)
}