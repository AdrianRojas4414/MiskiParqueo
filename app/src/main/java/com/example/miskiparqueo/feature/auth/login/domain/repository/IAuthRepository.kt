package com.example.miskiparqueo.feature.auth.login.domain.repository

import com.example.miskiparqueo.feature.auth.domain.model.UserModel
import com.example.miskiparqueo.feature.auth.signup.domain.model.vo.Password

interface IAuthRepository {
    suspend fun login(credential: String, password: Password): Result<UserModel>
    suspend fun getUserById(userId: String): Result<UserModel>
}