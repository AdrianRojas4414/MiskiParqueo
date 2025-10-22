package com.example.miskiparqueo.di

import com.example.miskiparqueo.feature.auth.domain.usecases.GetUserByIdUseCase // <-- AÑADE ESTE IMPORT
import com.example.miskiparqueo.feature.auth.login.data.datasource.LoginFirebaseDataSource
import com.example.miskiparqueo.feature.auth.login.data.repository.AuthRepositoryImpl
import com.example.miskiparqueo.feature.auth.login.domain.repository.IAuthRepository
import com.example.miskiparqueo.feature.auth.login.domain.usecases.LoginUseCase
import com.example.miskiparqueo.feature.auth.login.presentation.LoginViewModel
import com.example.miskiparqueo.feature.auth.signup.data.datasource.SignUpFirebaseDataSource
import com.example.miskiparqueo.feature.auth.signup.data.repository.SignUpRepositoryImpl
import com.example.miskiparqueo.feature.auth.signup.domain.repository.ISignUpRepository
import com.example.miskiparqueo.feature.auth.signup.domain.usecases.SignUpUseCase
import com.example.miskiparqueo.feature.auth.signup.presentation.SignUpViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val authModule = module {

    //================================================
    // SIGN UP DEPENDENCIES (Firebase)
    //================================================

    // DataSource - Firebase
    single { SignUpFirebaseDataSource() }

    // Repository
    single<ISignUpRepository> { SignUpRepositoryImpl(get()) }

    // UseCase
    single { SignUpUseCase(get()) }

    // ViewModel
    viewModel { SignUpViewModel(get()) }


    //================================================
    // LOG IN & USER DATA DEPENDENCIES (Firebase)
    //================================================

    // DataSource - Firebase
    single { LoginFirebaseDataSource() }

    // Repository
    single<IAuthRepository> { AuthRepositoryImpl(get()) }

    // UseCases
    single { LoginUseCase(get()) }
    single { GetUserByIdUseCase(get()) } // <-- AÑADE ESTA LÍNEA

    // ViewModel
    viewModel { LoginViewModel(get()) }
}