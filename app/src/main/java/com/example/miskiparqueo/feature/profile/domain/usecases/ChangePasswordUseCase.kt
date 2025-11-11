package com.example.miskiparqueo.feature.profile.domain.usecases

import com.example.miskiparqueo.feature.profile.domain.repository.IProfileRepository

class ChangePasswordUseCase(private val repository: IProfileRepository) {
    suspend operator fun invoke(userId: String, currentPassword: String, newPassword: String) =
        repository.changePassword(userId, currentPassword, newPassword)
}