package com.miskidev.miskiparqueo.feature.profile.presentation

import com.miskidev.miskiparqueo.feature.profile.domain.usecases.ChangePasswordUseCase
import com.miskidev.miskiparqueo.feature.profile.presentation.changepassword.ChangePasswordViewModel
import com.miskidev.miskiparqueo.feature.profile.domain.repository.IProfileRepository
import com.miskidev.miskiparqueo.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChangePasswordViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `passwords que no coinciden generan error inmediato`() {
        val vm = ChangePasswordViewModel(ChangePasswordUseCase(FakeProfileRepository(Result.success(Unit))))

        vm.changePassword("u1", "old", "new", "different")

        assertTrue(vm.uiState.value is ChangePasswordViewModel.ChangePasswordUIState.Error)
    }

    @Test
    fun `password corta genera error inmediato`() {
        val vm = ChangePasswordViewModel(ChangePasswordUseCase(FakeProfileRepository(Result.success(Unit))))

        vm.changePassword("u1", "old", "123", "123")

        assertTrue(vm.uiState.value is ChangePasswordViewModel.ChangePasswordUIState.Error)
    }

    @Test
    fun `exito actualiza estado success`() = runTest {
        val vm = ChangePasswordViewModel(
            ChangePasswordUseCase(FakeProfileRepository(Result.success(Unit)))
        )

        vm.changePassword("u1", "old", "123456", "123456")
        advanceUntilIdle()

        assertTrue(vm.uiState.value is ChangePasswordViewModel.ChangePasswordUIState.Success)
    }

    @Test
    fun `fallo actualiza estado error`() = runTest {
        val vm = ChangePasswordViewModel(
            ChangePasswordUseCase(FakeProfileRepository(Result.failure(IllegalStateException("boom"))))
        )

        vm.changePassword("u1", "old", "123456", "123456")
        advanceUntilIdle()

        assertTrue(vm.uiState.value is ChangePasswordViewModel.ChangePasswordUIState.Error)
    }

    private class FakeProfileRepository(
        private val result: Result<Unit>
    ) : IProfileRepository {
        override suspend fun changePassword(
            userId: String,
            currentPassword: String,
            newPassword: String
        ): Result<Unit> = result

        override suspend fun updateUser(user: com.miskidev.miskiparqueo.feature.auth.domain.model.UserModel): Result<Unit> =
            Result.success(Unit)
    }
}
