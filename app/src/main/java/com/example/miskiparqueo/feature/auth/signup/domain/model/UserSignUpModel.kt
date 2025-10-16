package com.example.miskiparqueo.feature.auth.signup.domain.model

import com.example.miskiparqueo.feature.auth.signup.domain.model.vo.Email
import com.example.miskiparqueo.feature.auth.signup.domain.model.vo.FirstName
import com.example.miskiparqueo.feature.auth.signup.domain.model.vo.LastName
import com.example.miskiparqueo.feature.auth.signup.domain.model.vo.Username

data class UserSignUpModel(
    val userId: String,
    val firstName: FirstName,
    val lastName: LastName,
    val username: Username,
    val email: Email
)