package com.miskidev.miskiparqueo.di

import com.miskidev.miskiparqueo.feature.profile.data.datasource.ProfileFirebaseDataSource
import com.miskidev.miskiparqueo.feature.profile.data.repository.ProfileRepositoryImpl
import com.miskidev.miskiparqueo.feature.profile.domain.repository.IProfileRepository
import com.miskidev.miskiparqueo.feature.profile.domain.usecases.ChangePasswordUseCase
import com.miskidev.miskiparqueo.feature.profile.domain.usecases.UpdateUserUseCase
import com.miskidev.miskiparqueo.feature.profile.presentation.changepassword.ChangePasswordViewModel
import com.miskidev.miskiparqueo.feature.profile.presentation.profile.ProfileViewModel
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