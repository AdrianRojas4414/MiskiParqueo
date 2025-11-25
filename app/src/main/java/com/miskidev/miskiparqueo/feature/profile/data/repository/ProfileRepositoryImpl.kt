package com.miskidev.miskiparqueo.feature.profile.data.repository

import com.miskidev.miskiparqueo.feature.auth.domain.model.UserModel
import com.miskidev.miskiparqueo.feature.profile.data.datasource.ProfileFirebaseDataSource
import com.miskidev.miskiparqueo.feature.profile.domain.repository.IProfileRepository

class ProfileRepositoryImpl(
    private val firebaseDataSource: ProfileFirebaseDataSource
) : IProfileRepository {

    override suspend fun updateUser(user: UserModel): Result<Unit> {
        return try {
            firebaseDataSource.updateUser(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun changePassword(
        userId: String,
        currentPassword: String,
        newPassword: String
    ): Result<Unit> {
        return try {
            firebaseDataSource.changePassword(userId, currentPassword, newPassword)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}