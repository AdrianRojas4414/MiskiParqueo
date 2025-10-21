package com.example.miskiparqueo.feature.auth.login.data.repository

import com.example.miskiparqueo.feature.auth.domain.model.UserModel
import com.example.miskiparqueo.feature.auth.login.data.datasource.LoginFirebaseDataSource
import com.example.miskiparqueo.feature.auth.login.data.datasource.LoginRemoteDataSource
import com.example.miskiparqueo.feature.auth.login.domain.repository.IAuthRepository
import com.example.miskiparqueo.feature.auth.signup.domain.model.vo.Email
import com.example.miskiparqueo.feature.auth.signup.domain.model.vo.FirstName
import com.example.miskiparqueo.feature.auth.signup.domain.model.vo.LastName
import com.example.miskiparqueo.feature.auth.signup.domain.model.vo.Password
import com.example.miskiparqueo.feature.auth.signup.domain.model.vo.Username

class AuthRepositoryImpl(
    private val firebaseDataSource: LoginFirebaseDataSource
) : IAuthRepository {

    override suspend fun login(credential: String, password: Password): Result<UserModel> {
        return try {
            val result = firebaseDataSource.login(credential, password.value)

            result.map { response ->
                // Mapeo del DTO de datos (LoginResponse) al Modelo de Dominio (User)
                UserModel(
                    userId = response.userId,
                    firstName = FirstName.create(response.firstName),
                    lastName = LastName.create(response.lastName),
                    username = Username.create(response.username),
                    email = Email.create(response.email)
                )
            }
        } catch (e: Exception) {
            // Capturar cualquier excepción y devolverla como un fallo
            Result.failure(e)
        }
    }
}