package com.example.miskiparqueo.feature.auth.signup.domain.repository

import com.example.miskiparqueo.feature.auth.signup.domain.model.UserSignUpModel
import com.example.miskiparqueo.feature.auth.signup.domain.model.vo.Email
import com.example.miskiparqueo.feature.auth.signup.domain.model.vo.FirstName
import com.example.miskiparqueo.feature.auth.signup.domain.model.vo.LastName
import com.example.miskiparqueo.feature.auth.signup.domain.model.vo.Password
import com.example.miskiparqueo.feature.auth.signup.domain.model.vo.Username

interface ISignUpRepository {
    suspend fun signUp(
        firstName: FirstName,
        lastName: LastName,
        username: Username,
        email: Email,
        password: Password
    ): Result<UserSignUpModel>
}