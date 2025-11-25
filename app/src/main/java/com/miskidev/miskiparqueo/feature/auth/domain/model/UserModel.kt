package com.miskidev.miskiparqueo.feature.auth.domain.model

import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.Email
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.FirstName
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.LastName
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.Username

data class UserModel(
    val userId: String,
    val firstName: FirstName,
    val lastName: LastName,
    val username: Username,
    val email: Email
)