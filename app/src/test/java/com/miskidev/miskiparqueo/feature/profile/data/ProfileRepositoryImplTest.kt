package com.miskidev.miskiparqueo.feature.profile.data

import com.miskidev.miskiparqueo.feature.auth.domain.model.UserModel
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.Email
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.FirstName
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.LastName
import com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo.Username
import com.miskidev.miskiparqueo.feature.profile.data.datasource.ProfileFirebaseDataSource
import com.miskidev.miskiparqueo.feature.profile.data.repository.ProfileRepositoryImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class ProfileRepositoryImplTest {

    @Test
    fun `updateUser delega al datasource`() = runBlocking {
        val fakeDataSource = mockk<ProfileFirebaseDataSource>()
        coEvery { fakeDataSource.updateUser(any()) } returns Result.success(Unit)
        coEvery { fakeDataSource.changePassword(any(), any(), any()) } returns Result.success(Unit)
        val repo = ProfileRepositoryImpl(fakeDataSource)

        val result = repo.updateUser(sampleUser())

        assertTrue(result.isSuccess)
        coVerify { fakeDataSource.updateUser(any()) }
    }

    @Test
    fun `changePassword retorna failure ante excepcion`() = runBlocking {
        val fakeDataSource = mockk<ProfileFirebaseDataSource>()
        coEvery { fakeDataSource.updateUser(any()) } returns Result.success(Unit)
        coEvery { fakeDataSource.changePassword(any(), any(), any()) } throws IllegalStateException("boom")
        val repo = ProfileRepositoryImpl(fakeDataSource)

        val result = repo.changePassword("u1", "old", "new")

        assertTrue(result.isFailure)
    }

    private fun sampleUser() = UserModel(
        userId = "u1",
        firstName = FirstName.create("Ana"),
        lastName = LastName.create("Lopez"),
        username = Username.create("analopez"),
        email = Email.create("ana@example.com")
    )
}
