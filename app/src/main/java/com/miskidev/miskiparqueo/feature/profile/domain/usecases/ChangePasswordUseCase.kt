package com.miskidev.miskiparqueo.feature.profile.domain.usecases

import com.miskidev.miskiparqueo.feature.profile.domain.repository.IProfileRepository

class ChangePasswordUseCase(private val repository: IProfileRepository) {
    suspend operator fun invoke(userId: String, currentPassword: String, newPassword: String) =
        repository.changePassword(userId, currentPassword, newPassword)
}