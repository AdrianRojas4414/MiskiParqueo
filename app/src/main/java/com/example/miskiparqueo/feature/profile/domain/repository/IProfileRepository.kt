package com.example.miskiparqueo.feature.profile.domain.repository

import com.example.miskiparqueo.feature.auth.domain.model.UserModel

interface IProfileRepository {
    suspend fun updateUser(user: UserModel): Result<Unit>
    suspend fun changePassword(userId: String, currentPassword: String, newPassword: String): Result<Unit>
}