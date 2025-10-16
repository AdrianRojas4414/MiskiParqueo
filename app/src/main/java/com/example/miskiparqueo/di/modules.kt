package com.example.miskiparqueo.di

import com.example.miskiparqueo.feature.auth.signup.data.datasource.SignUpRemoteDataSource
import com.example.miskiparqueo.feature.auth.signup.data.repository.SignUpRepositoryImpl
import com.example.miskiparqueo.feature.auth.signup.domain.repository.ISignUpRepository
import com.example.miskiparqueo.feature.auth.signup.domain.usecases.SignUpUseCase
import com.example.miskiparqueo.feature.auth.signup.presentation.SignUpViewModel
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel

val appModule = module() {

    //SIGNUP
    // DataSource
    single { SignUpRemoteDataSource() }

    // Repository
    single<ISignUpRepository> { SignUpRepositoryImpl(get()) }

    // UseCase
    single { SignUpUseCase(get()) }

    // ViewModel
    viewModel { SignUpViewModel(get()) }
}