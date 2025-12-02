package com.miskidev.miskiparqueo.feature.auth.login.domain.usecases

import com.miskidev.miskiparqueo.feature.auth.domain.model.UserModel
import com.miskidev.miskiparqueo.feature.auth.login.domain.repository.IAuthRepository
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.Email
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.FirstName
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.LastName
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.Password
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.Username
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LoginUseCaseTest {

    @MockK
    lateinit var repository: IAuthRepository

    private lateinit var useCase: LoginUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = LoginUseCase(repository)
    }

    @Test
    fun `credential vacia falla`() = runTest {
        val result = useCase("", Password.create("Password1"))

        assertTrue(result.isFailure)
        coVerify(exactly = 0) { repository.login(any(), any()) }
    }

    @Test
    fun `login exitoso delega`() = runTest {
        val password = Password.create("Password1")
        val expectedUser = aUser()
        coEvery { repository.login("user", password) } returns Result.success(expectedUser)

        val result = useCase("user", password)

        assertTrue(result.isSuccess)
        assertEquals(expectedUser, result.getOrNull())
        coVerify(exactly = 1) { repository.login("user", password) }
    }

    @Test
    fun `error repositorio se propaga`() = runTest {
        val password = Password.create("Password1")
        coEvery { repository.login("user", password) } returns Result.failure(IllegalStateException("bad credentials"))

        val result = useCase("user", password)

        assertTrue(result.isFailure)
        coVerify(exactly = 1) { repository.login("user", password) }
    }

    @Test
    fun `credential solo espacios falla`() = runTest {
        val password = Password.create("Password1")

        val result = useCase("   ", password)

        assertTrue(result.isFailure)
        coVerify(exactly = 0) { repository.login(any(), any()) }
    }

    private fun aUser() = UserModel(
        userId = "1",
        firstName = FirstName.create("Jane"),
        lastName = LastName.create("Doe"),
        username = Username.create("jane"),
        email = Email.create("jane@example.com")
    )
}
