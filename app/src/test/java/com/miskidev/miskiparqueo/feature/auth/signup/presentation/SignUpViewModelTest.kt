package com.miskidev.miskiparqueo.feature.auth.signup.presentation

import com.miskidev.miskiparqueo.MainDispatcherRule
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.UserSignUpModel
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.Email
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.FirstName
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.LastName
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.Password
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.Username
import com.miskidev.miskiparqueo.feature.auth.signup.domain.repository.ISignUpRepository
import com.miskidev.miskiparqueo.feature.auth.signup.domain.usecases.SignUpUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SignUpViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `email invalido da validacion`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val viewModel = SignUpViewModel(
            signUpUseCase = SignUpUseCase(FakeSignUpRepository(Result.failure(Exception("should not be called")))),
            dispatcher = dispatcher
        )

        viewModel.signUp("Ana", "Lopez", "analopez", "bad-email", "Password1")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is SignUpViewModel.SignUpStateUI.ValidationError)
        val validation = state as SignUpViewModel.SignUpStateUI.ValidationError
        assertTrue(validation.emailError?.contains("email", ignoreCase = true) == true)
    }

    @Test
    fun `signup exitoso actualiza estado`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val expectedUser = userModel()
        val viewModel = SignUpViewModel(
            signUpUseCase = SignUpUseCase(FakeSignUpRepository(Result.success(expectedUser))),
            dispatcher = dispatcher
        )

        viewModel.signUp(
            firstNameRaw = "Ana",
            lastNameRaw = "Lopez",
            usernameRaw = "analopez",
            emailRaw = "ana@example.com",
            passwordRaw = "Password1"
        )
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is SignUpViewModel.SignUpStateUI.Success)
        assertEquals(expectedUser, (state as SignUpViewModel.SignUpStateUI.Success).user)
    }

    @Test
    fun `password debil da validacion`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val viewModel = SignUpViewModel(
            signUpUseCase = SignUpUseCase(FakeSignUpRepository(Result.failure(Exception("should not be called")))),
            dispatcher = dispatcher
        )

        viewModel.signUp(
            firstNameRaw = "Ana",
            lastNameRaw = "Lopez",
            usernameRaw = "analopez",
            emailRaw = "ana@example.com",
            passwordRaw = "short"
        )
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is SignUpViewModel.SignUpStateUI.ValidationError)
    }

    private fun userModel() = UserSignUpModel(
        userId = "123",
        firstName = FirstName.create("Ana"),
        lastName = LastName.create("Lopez"),
        username = Username.create("analopez"),
        email = Email.create("ana@example.com")
    )

    private class FakeSignUpRepository(
        private val result: Result<UserSignUpModel>
    ) : ISignUpRepository {
        override suspend fun signUp(
            firstName: FirstName,
            lastName: LastName,
            username: Username,
            email: Email,
            password: Password
        ): Result<UserSignUpModel> = result
    }
}
