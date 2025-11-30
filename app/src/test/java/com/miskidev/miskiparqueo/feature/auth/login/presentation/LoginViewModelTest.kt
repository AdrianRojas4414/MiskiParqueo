package com.miskidev.miskiparqueo.feature.auth.login.presentation

import com.miskidev.miskiparqueo.MainDispatcherRule
import com.miskidev.miskiparqueo.feature.auth.domain.model.UserModel
import com.miskidev.miskiparqueo.feature.auth.login.domain.repository.IAuthRepository
import com.miskidev.miskiparqueo.feature.auth.login.domain.usecases.LoginUseCase
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.Email
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.FirstName
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.LastName
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.Password
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.Username
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `password invalida da validacion`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val viewModel = LoginViewModel(
            loginUseCase = LoginUseCase(FakeAuthRepository(Result.failure(Exception("should not be called")))),
            dispatcher = dispatcher
        )

        viewModel.login("user@example.com", "short")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is LoginViewModel.LoginStateUI.ValidationError)
        assertTrue((state as LoginViewModel.LoginStateUI.ValidationError).message.contains("contrase", ignoreCase = true))
    }

    @Test
    fun `login exitoso actualiza estado`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val expectedUser = aUser()
        val viewModel = LoginViewModel(
            loginUseCase = LoginUseCase(FakeAuthRepository(Result.success(expectedUser))),
            dispatcher = dispatcher
        )

        viewModel.login("user@example.com", "Password1")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is LoginViewModel.LoginStateUI.Success)
        assertEquals(expectedUser, (state as LoginViewModel.LoginStateUI.Success).user)
    }

    @Test
    fun `error repositorio en estado`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val viewModel = LoginViewModel(
            loginUseCase = LoginUseCase(FakeAuthRepository(Result.failure(IllegalStateException("boom")))),
            dispatcher = dispatcher
        )

        viewModel.login("user@example.com", "Password1")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is LoginViewModel.LoginStateUI.Error)
    }

    private fun aUser() = UserModel(
        userId = "1",
        firstName = FirstName.create("Jane"),
        lastName = LastName.create("Doe"),
        username = Username.create("jane_d"),
        email = Email.create("user@example.com")
    )

    private class FakeAuthRepository(
        private val result: Result<UserModel>
    ) : IAuthRepository {
        override suspend fun login(credential: String, password: Password): Result<UserModel> = result

        override suspend fun getUserById(userId: String): Result<UserModel> = result
    }
}
