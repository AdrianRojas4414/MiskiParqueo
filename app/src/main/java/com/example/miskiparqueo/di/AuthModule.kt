package com.example.miskiparqueo.di

import com.example.miskiparqueo.feature.auth.login.data.datasource.LoginRemoteDataSource
import com.example.miskiparqueo.feature.auth.login.data.repository.AuthRepositoryImpl
import com.example.miskiparqueo.feature.auth.login.domain.repository.IAuthRepository
import com.example.miskiparqueo.feature.auth.login.domain.usecases.LoginUseCase
import com.example.miskiparqueo.feature.auth.login.presentation.LoginViewModel
import com.example.miskiparqueo.feature.auth.signup.data.datasource.SignUpRemoteDataSource
import com.example.miskiparqueo.feature.auth.signup.data.repository.SignUpRepositoryImpl
import com.example.miskiparqueo.feature.auth.signup.domain.repository.ISignUpRepository
import com.example.miskiparqueo.feature.auth.signup.domain.usecases.SignUpUseCase
import com.example.miskiparqueo.feature.auth.signup.presentation.SignUpViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val authModule = module {

    //================================================
    // SIGN UP DEPENDENCIES
    //================================================

    // DataSource
    single { SignUpRemoteDataSource() }

    // Repository
    single<ISignUpRepository> { SignUpRepositoryImpl(get()) }

    // UseCase
    single { SignUpUseCase(get()) }

    // ViewModel
    viewModel { SignUpViewModel(get()) }


    //================================================
    // LOG IN DEPENDENCIES
    //================================================

    // DataSource
    single { LoginRemoteDataSource() }

    // Repository
    single<IAuthRepository> { AuthRepositoryImpl(get()) }

    // UseCase
    single { LoginUseCase(get()) }

    // ViewModel
    viewModel { LoginViewModel(get()) }
}