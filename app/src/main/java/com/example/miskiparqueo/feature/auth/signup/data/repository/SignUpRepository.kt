package com.example.miskiparqueo.feature.auth.signup.data.repository

import com.example.miskiparqueo.feature.auth.signup.data.datasource.SignUpFirebaseDataSource
import com.example.miskiparqueo.feature.auth.signup.data.datasource.SignUpRemoteDataSource
import com.example.miskiparqueo.feature.auth.signup.domain.model.UserSignUpModel
import com.example.miskiparqueo.feature.auth.signup.domain.model.vo.Email
import com.example.miskiparqueo.feature.auth.signup.domain.model.vo.FirstName
import com.example.miskiparqueo.feature.auth.signup.domain.model.vo.LastName
import com.example.miskiparqueo.feature.auth.signup.domain.model.vo.Password
import com.example.miskiparqueo.feature.auth.signup.domain.model.vo.Username
import com.example.miskiparqueo.feature.auth.signup.domain.repository.ISignUpRepository

class SignUpRepositoryImpl(
    private val firebaseDataSource: SignUpFirebaseDataSource
) : ISignUpRepository {

    override suspend fun signUp(
        firstName: FirstName,
        lastName: LastName,
        username: Username,
        email: Email,
        password: Password
    ): Result<UserSignUpModel> {
        return try {
            val result = firebaseDataSource.signUp(
                firstName = firstName.value,
                lastName = lastName.value,
                username = username,
                email = email,
                password = password.value
            )

            result.map { response ->
                UserSignUpModel(
                    userId = response.userId,
                    firstName = FirstName.create(response.firstName),
                    lastName = LastName.create(response.lastName),
                    username = Username.create(response.username),
                    email = Email.create(response.email)
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}