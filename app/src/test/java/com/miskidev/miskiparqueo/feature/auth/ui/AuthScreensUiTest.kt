package com.miskidev.miskiparqueo.feature.auth.ui

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import android.app.Application
import com.miskidev.miskiparqueo.BuildConfig
import com.miskidev.miskiparqueo.MainDispatcherRule
import com.miskidev.miskiparqueo.feature.auth.domain.model.UserModel
import com.miskidev.miskiparqueo.feature.auth.login.domain.repository.IAuthRepository
import com.miskidev.miskiparqueo.feature.auth.login.domain.usecases.LoginUseCase
import com.miskidev.miskiparqueo.feature.auth.login.presentation.LoginScreen
import com.miskidev.miskiparqueo.feature.auth.login.presentation.LoginViewModel
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.UserSignUpModel
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.Email
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.FirstName
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.LastName
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.Password
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.Username
import com.miskidev.miskiparqueo.feature.auth.signup.domain.repository.ISignUpRepository
import com.miskidev.miskiparqueo.feature.auth.signup.domain.usecases.SignUpUseCase
import com.miskidev.miskiparqueo.feature.auth.signup.presentation.SignUpScreen
import com.miskidev.miskiparqueo.feature.auth.signup.presentation.SignUpViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runners.model.Statement
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.junit.runner.RunWith

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = Application::class)
@OptIn(ExperimentalCoroutinesApi::class)
class AuthScreensUiTest {

    private val mainDispatcherRule = MainDispatcherRule()
    private val composeTestRule = createComposeRule()
    private val debugOnlyRule = TestRule { base, _ ->
        object : Statement() {
            override fun evaluate() {
                if (!BuildConfig.DEBUG) {
                    org.junit.Assume.assumeTrue("UI tests run only on debug variants", false)
                }
                base.evaluate()
            }
        }
    }

    @get:Rule
    val ruleChain: TestRule = RuleChain
        .outerRule(debugOnlyRule)
        .around(mainDispatcherRule)
        .around(composeTestRule)

    @Test
    fun loginScreenMuestraErrorPasswordCorta() {
        val dispatcher = UnconfinedTestDispatcher()
        val viewModel = LoginViewModel(
            loginUseCase = LoginUseCase(FakeAuthRepository(Result.failure(Exception("unused")))),
            dispatcher = dispatcher
        )

        composeTestRule.setContent {
            LoginScreen(
                vm = viewModel,
                onNavigateToSignUp = {},
                onNavigateToMap = {}
            )
        }

        composeTestRule.onNodeWithText("Email o Username").performTextInput("user@example.com")
        composeTestRule.onAllNodes(hasSetTextAction())[1].performTextInput("short")
        composeTestRule.onNodeWithText("Ingresa").performClick()

        dispatcher.scheduler.advanceUntilIdle()
        composeTestRule.waitForIdle()

        composeTestRule.onNode(hasText("8 caracteres", ignoreCase = true, substring = true))
            .assertIsDisplayed()
    }

    @Test
    fun loginScreenNavegaEnExito() {
        val dispatcher = UnconfinedTestDispatcher()
        val expectedUser = loginUser()
        val viewModel = LoginViewModel(
            loginUseCase = LoginUseCase(FakeAuthRepository(Result.success(expectedUser))),
            dispatcher = dispatcher
        )
        var navigatedUserId: String? = null

        composeTestRule.setContent {
            LoginScreen(
                vm = viewModel,
                onNavigateToSignUp = {},
                onNavigateToMap = { userId -> navigatedUserId = userId }
            )
        }

        composeTestRule.onNodeWithText("Email o Username").performTextInput("user@example.com")
        composeTestRule.onAllNodes(hasSetTextAction())[1].performTextInput("Password1")
        composeTestRule.onNodeWithText("Ingresa").performClick()

        dispatcher.scheduler.advanceUntilIdle()
        composeTestRule.waitForIdle()

        requireNotNull(navigatedUserId)
        org.junit.Assert.assertEquals(expectedUser.userId, navigatedUserId)
    }

    @Test
    fun signUpScreenMuestraErrorEmail() {
        val dispatcher = UnconfinedTestDispatcher()
        val viewModel = SignUpViewModel(
            signUpUseCase = SignUpUseCase(FakeSignUpRepository(Result.failure(Exception("unused")))),
            dispatcher = dispatcher
        )

        composeTestRule.setContent {
            SignUpScreen(
                vm = viewModel,
                onNavigateToLogin = {},
                onNavigateToMap = {}
            )
        }

        composeTestRule.onNodeWithText("Primer Nombre").performTextInput("Ana")
        composeTestRule.onNodeWithText("Apellido").performTextInput("Lopez")
        composeTestRule.onNodeWithText("Username").performTextInput("analopez")
        composeTestRule.onNodeWithText("Email").performTextInput("bad-email")
        composeTestRule.onAllNodes(hasSetTextAction())[4].performTextInput("Password1")

        composeTestRule.onNodeWithText("Registrarse").performClick()

        dispatcher.scheduler.advanceUntilIdle()
        composeTestRule.waitForIdle()

        composeTestRule.onNode(hasText("email", ignoreCase = true, substring = true))
            .assertIsDisplayed()
    }

    @Test
    fun signUpScreenMuestraInputs() {
        val viewModel = SignUpViewModel(
            signUpUseCase = SignUpUseCase(FakeSignUpRepository(Result.failure(Exception("unused")))),
            dispatcher = UnconfinedTestDispatcher()
        )

        composeTestRule.setContent {
            SignUpScreen(
                vm = viewModel,
                onNavigateToLogin = {},
                onNavigateToMap = {}
            )
        }

        composeTestRule.onAllNodes(hasSetTextAction()).assertCountEquals(5)
        composeTestRule.onAllNodes(hasText("Registrarse", ignoreCase = true)).assertCountEquals(1)
    }

    @Test
    fun loginScreenMuestraInputsYBoton() {
        val viewModel = LoginViewModel(
            loginUseCase = LoginUseCase(FakeAuthRepository(Result.failure(Exception("unused")))),
            dispatcher = UnconfinedTestDispatcher()
        )

        composeTestRule.setContent {
            LoginScreen(
                vm = viewModel,
                onNavigateToSignUp = {},
                onNavigateToMap = {}
            )
        }

        composeTestRule.onNodeWithText("Email o Username").assertIsDisplayed()
        composeTestRule.onNode(hasText("Contrase", substring = true, ignoreCase = true)).assertIsDisplayed()
        composeTestRule.onNodeWithText("Ingresa").assertIsDisplayed()
    }

    private fun loginUser() = UserModel(
        userId = "42",
        firstName = FirstName.create("Jane"),
        lastName = LastName.create("Doe"),
        username = Username.create("jane_d"),
        email = Email.create("user@example.com")
    )

    private class FakeAuthRepository(
        private val loginResult: Result<UserModel>
    ) : IAuthRepository {
        override suspend fun login(credential: String, password: Password): Result<UserModel> = loginResult

        override suspend fun getUserById(userId: String): Result<UserModel> = loginResult
    }

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
