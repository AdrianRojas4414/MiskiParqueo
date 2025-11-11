package com.example.miskiparqueo.di

import com.example.miskiparqueo.feature.profile.data.datasource.ProfileFirebaseDataSource
import com.example.miskiparqueo.feature.profile.data.repository.ProfileRepositoryImpl
import com.example.miskiparqueo.feature.profile.domain.repository.IProfileRepository
import com.example.miskiparqueo.feature.profile.domain.usecases.ChangePasswordUseCase
import com.example.miskiparqueo.feature.profile.domain.usecases.UpdateUserUseCase
import com.example.miskiparqueo.feature.profile.presentation.changepassword.ChangePasswordViewModel
import com.example.miskiparqueo.feature.profile.presentation.profile.ProfileViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val profileModule = module {
    // DataSource
    single { ProfileFirebaseDataSource() }

    // Repository
    single<IProfileRepository> { ProfileRepositoryImpl(get()) }

    // UseCases
    factory { UpdateUserUseCase(get()) }
    factory { ChangePasswordUseCase(get()) }

    // ViewModels
    viewModel { ChangePasswordViewModel(get()) }
    viewModel { ProfileViewModel(get(), get()) } // Recibe UpdateUserUseCase y GetUserByIdUseCase
}