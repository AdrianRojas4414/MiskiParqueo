package com.miskidev.miskiparqueo.feature.profile.presentation

import com.miskidev.miskiparqueo.feature.auth.domain.model.UserModel
import com.miskidev.miskiparqueo.feature.auth.domain.usecases.GetUserByIdUseCase
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.Email
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.FirstName
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.LastName
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.Password
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.Username
import com.miskidev.miskiparqueo.feature.auth.login.domain.repository.IAuthRepository
import com.miskidev.miskiparqueo.feature.profile.domain.repository.IProfileRepository
import com.miskidev.miskiparqueo.feature.profile.domain.usecases.UpdateUserUseCase
import com.miskidev.miskiparqueo.feature.profile.presentation.profile.ProfileViewModel
import com.miskidev.miskiparqueo.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loadUserById success actualiza estado`() = runTest {
        val user = sampleUser()
        val vm = ProfileViewModel(
            updateUserUseCase = UpdateUserUseCase(SuccessProfileRepo()),
            getUserByIdUseCase = GetUserByIdUseCase(SingleUserRepo(Result.success(user)))
        )

        vm.loadUserById("u1")
        advanceUntilIdle()

        assertEquals(user, vm.userState.value)
        assertTrue(vm.uiState.value is ProfileViewModel.ProfileUIState.Idle)
    }

    @Test
    fun `loadUserById failure deja error`() = runTest {
        val vm = ProfileViewModel(
            updateUserUseCase = UpdateUserUseCase(SuccessProfileRepo()),
            getUserByIdUseCase = GetUserByIdUseCase(SingleUserRepo(Result.failure(IllegalStateException("fail"))))
        )

        vm.loadUserById("u1")
        advanceUntilIdle()

        assertTrue(vm.uiState.value is ProfileViewModel.ProfileUIState.Error)
    }

    @Test
    fun `saveChanges exito emite success`() = runTest {
        val user = sampleUser()
        val vm = ProfileViewModel(
            updateUserUseCase = UpdateUserUseCase(SuccessProfileRepo()),
            getUserByIdUseCase = GetUserByIdUseCase(SingleUserRepo(Result.success(user)))
        )

        vm.loadUserById("u1")
        advanceUntilIdle()
        vm.onFirstNameChanged("Ana Maria")
        vm.saveChanges()
        advanceUntilIdle()

        assertTrue(vm.uiState.value is ProfileViewModel.ProfileUIState.Success)
    }

    @Test
    fun `saveChanges error emite error`() = runTest {
        val user = sampleUser()
        val vm = ProfileViewModel(
            updateUserUseCase = UpdateUserUseCase(FailingProfileRepo()),
            getUserByIdUseCase = GetUserByIdUseCase(SingleUserRepo(Result.success(user)))
        )

        vm.loadUserById("u1")
        advanceUntilIdle()
        vm.saveChanges()
        advanceUntilIdle()

        assertTrue(vm.uiState.value is ProfileViewModel.ProfileUIState.Error)
    }

    private fun sampleUser() = UserModel(
        userId = "u1",
        firstName = FirstName.create("Ana"),
        lastName = LastName.create("Lopez"),
        username = Username.create("analopez"),
        email = Email.create("ana@example.com")
    )

    private class SuccessProfileRepo : IProfileRepository {
        var lastUser: UserModel? = null
        override suspend fun changePassword(userId: String, currentPassword: String, newPassword: String): Result<Unit> =
            Result.success(Unit)

        override suspend fun updateUser(user: UserModel): Result<Unit> {
            lastUser = user
            return Result.success(Unit)
        }
    }

    private class FailingProfileRepo : IProfileRepository {
        override suspend fun changePassword(userId: String, currentPassword: String, newPassword: String): Result<Unit> =
            Result.success(Unit)

        override suspend fun updateUser(user: UserModel): Result<Unit> =
            Result.failure(IllegalArgumentException("bad"))
    }

    private class SingleUserRepo(
        private val result: Result<UserModel>
    ) : IAuthRepository {
        override suspend fun login(credential: String, password: com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.Password): Result<UserModel> =
            result

        override suspend fun getUserById(userId: String): Result<UserModel> = result
    }
}
