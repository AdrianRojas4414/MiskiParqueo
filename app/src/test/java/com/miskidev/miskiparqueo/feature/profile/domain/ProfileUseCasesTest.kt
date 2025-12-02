package com.miskidev.miskiparqueo.feature.profile.domain

import com.miskidev.miskiparqueo.feature.auth.domain.model.UserModel
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.Email
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.FirstName
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.LastName
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.Username
import com.miskidev.miskiparqueo.feature.profile.domain.repository.IProfileRepository
import com.miskidev.miskiparqueo.feature.profile.domain.usecases.ChangePasswordUseCase
import com.miskidev.miskiparqueo.feature.profile.domain.usecases.UpdateUserUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProfileUseCasesTest {

    @Test
    fun `cambio password delega en repo`() = runTest {
        val repo = FakeProfileRepository()
        val useCase = ChangePasswordUseCase(repo)

        val result = useCase("u1", "old", "new")

        assertTrue(result.isSuccess)
        assertEquals("u1" to ("old" to "new"), repo.lastPasswordChange)
    }

    @Test
    fun `actualizar usuario delega en repo`() = runTest {
        val repo = FakeProfileRepository()
        val useCase = UpdateUserUseCase(repo)
        val user = aUser()

        val result = useCase(user)

        assertTrue(result.isSuccess)
        assertEquals(user, repo.lastUpdatedUser)
    }

    private fun aUser() = UserModel(
        userId = "u1",
        firstName = FirstName.create("Ana"),
        lastName = LastName.create("Lopez"),
        username = Username.create("analopez"),
        email = Email.create("ana@example.com")
    )

    private class FakeProfileRepository : IProfileRepository {
        var lastPasswordChange: Pair<String, Pair<String, String>>? = null
        var lastUpdatedUser: UserModel? = null

        override suspend fun changePassword(userId: String, currentPassword: String, newPassword: String): Result<Unit> {
            lastPasswordChange = userId to (currentPassword to newPassword)
            return Result.success(Unit)
        }

        override suspend fun updateUser(user: UserModel): Result<Unit> {
            lastUpdatedUser = user
            return Result.success(Unit)
        }
    }
}
