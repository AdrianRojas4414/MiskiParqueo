package com.miskidev.miskiparqueo.di

import com.miskidev.miskiparqueo.feature.auth.login.data.datasource.LoginFirebaseDataSource
import com.miskidev.miskiparqueo.feature.auth.login.data.repository.AuthRepositoryImpl
import com.miskidev.miskiparqueo.feature.auth.login.domain.repository.IAuthRepository
import com.miskidev.miskiparqueo.feature.auth.login.domain.usecases.LoginUseCase
import com.miskidev.miskiparqueo.feature.auth.login.presentation.LoginViewModel
import com.miskidev.miskiparqueo.feature.auth.signup.data.datasource.SignUpFirebaseDataSource
import com.miskidev.miskiparqueo.feature.auth.signup.data.repository.SignUpRepositoryImpl
import com.miskidev.miskiparqueo.feature.auth.signup.domain.repository.ISignUpRepository
import com.miskidev.miskiparqueo.feature.auth.signup.domain.usecases.SignUpUseCase
import com.miskidev.miskiparqueo.feature.auth.signup.presentation.SignUpViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import com.miskidev.miskiparqueo.feature.auth.domain.usecases.GetUserByIdUseCase

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
    // LOG IN DEPENDENCIES (Firebase)
    //================================================

    // DataSource - Firebase
    single { LoginFirebaseDataSource    () }

    // Repository
    single<IAuthRepository> { AuthRepositoryImpl(get()) }
    // UseCase
    single { LoginUseCase(get()) }
    single { GetUserByIdUseCase(get()) }
    // ViewModel
    viewModel { LoginViewModel(get()) }
}