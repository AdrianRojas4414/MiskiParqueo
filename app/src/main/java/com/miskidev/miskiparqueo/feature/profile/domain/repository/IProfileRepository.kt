package com.miskidev.miskiparqueo.feature.profile.domain.repository

import com.miskidev.miskiparqueo.feature.auth.domain.model.UserModel

interface IProfileRepository {
    suspend fun updateUser(user: UserModel): Result<Unit>
    suspend fun changePassword(userId: String, currentPassword: String, newPassword: String): Result<Unit>
}