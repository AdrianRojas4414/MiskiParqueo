package com.example.miskiparqueo.feature.auth.signup.domain.usecases

import com.example.miskiparqueo.feature.auth.signup.domain.model.UserSignUpModel
import com.example.miskiparqueo.feature.auth.signup.domain.model.vo.Email
import com.example.miskiparqueo.feature.auth.signup.domain.model.vo.FirstName
import com.example.miskiparqueo.feature.auth.signup.domain.model.vo.LastName
import com.example.miskiparqueo.feature.auth.signup.domain.model.vo.Password
import com.example.miskiparqueo.feature.auth.signup.domain.model.vo.Username
import com.example.miskiparqueo.feature.auth.signup.domain.repository.ISignUpRepository

class SignUpUseCase(
    private val repository: ISignUpRepository
) {
    suspend operator fun invoke(
        firstName: FirstName,
        lastName: LastName,
        username: Username,
        email: Email,
        password: Password
    ): Result<UserSignUpModel> {
        return repository.signUp(firstName, lastName, username, email, password)
    }
}