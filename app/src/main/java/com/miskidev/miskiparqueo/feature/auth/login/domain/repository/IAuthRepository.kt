package com.miskidev.miskiparqueo.feature.auth.login.domain.repository

import com.miskidev.miskiparqueo.feature.auth.domain.model.UserModel
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.Password

interface IAuthRepository {
    suspend fun login(credential: String, password: Password): Result<UserModel>
    suspend fun getUserById(userId: String): Result<UserModel>
}