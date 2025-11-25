package com.miskidev.miskiparqueo.feature.auth.signup.domain.repository

import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.UserSignUpModel
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.Email
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.FirstName
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.LastName
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.Password
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.Username

interface ISignUpRepository {
    suspend fun signUp(
        firstName: FirstName,
        lastName: LastName,
        username: Username,
        email: Email,
        password: Password
    ): Result<UserSignUpModel>
}