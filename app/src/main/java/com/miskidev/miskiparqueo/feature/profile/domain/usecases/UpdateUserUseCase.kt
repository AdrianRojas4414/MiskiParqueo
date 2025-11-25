package com.miskidev.miskiparqueo.feature.profile.domain.usecases

import com.miskidev.miskiparqueo.feature.auth.domain.model.UserModel
import com.miskidev.miskiparqueo.feature.profile.domain.repository.IProfileRepository

class UpdateUserUseCase(private val repository: IProfileRepository) {
    suspend operator fun invoke(user: UserModel) = repository.updateUser(user)
}