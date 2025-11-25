package com.miskidev.miskiparqueo.feature.auth.signup.domain.usecases

import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.UserSignUpModel
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.Email
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.FirstName
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.LastName
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.Password
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.Username
import com.miskidev.miskiparqueo.feature.auth.signup.domain.repository.ISignUpRepository

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