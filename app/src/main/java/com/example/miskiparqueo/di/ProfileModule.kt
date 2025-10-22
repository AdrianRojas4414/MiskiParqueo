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

    // V-- ESTA ES LA LÍNEA CORREGIDA --V
    // Le decimos a Koin que inyecte AMBAS dependencias que ProfileViewModel necesita.
    // Koin es lo suficientemente inteligente como para saber cuál 'get()' corresponde a cada parámetro.
    viewModel { ProfileViewModel(get(), get()) }
}