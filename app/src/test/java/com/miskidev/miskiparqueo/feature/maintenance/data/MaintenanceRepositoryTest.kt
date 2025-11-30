package com.miskidev.miskiparqueo.feature.maintenance.data

import com.google.android.gms.tasks.Tasks
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.miskidev.miskiparqueo.R
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.verify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], manifest = Config.NONE, application = android.app.Application::class)
class MaintenanceRepositoryTest {

    @MockK(relaxed = true)
    lateinit var remoteConfig: FirebaseRemoteConfig

    @MockK(relaxed = true)
    lateinit var dataStore: MaintenanceDataStore

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `fetchMaintenanceStatus guarda y retorna flag`() = runBlocking {
        every { remoteConfig.fetchAndActivate() } returns Tasks.forResult(true)
        every { remoteConfig.getBoolean("maintenance_mode") } returns true
        coEvery { dataStore.getMaintenanceMode() } returns flowOf(false)
        coEvery { dataStore.setMaintenanceMode(any<Boolean>()) } returns Unit

        val repo = MaintenanceRepository(remoteConfig, dataStore, initRemote = false)

        val result = repo.fetchMaintenanceStatus().first()

        assertEquals(true, result)
        coVerify { dataStore.setMaintenanceMode(true) }
    }

    @Test
    fun `observeMaintenanceStatus delega al datastore`() = runBlocking {
        coEvery { dataStore.getMaintenanceMode() } returns flowOf(true)

        val repo = MaintenanceRepository(remoteConfig, dataStore, initRemote = false)

        val value = repo.observeMaintenanceStatus().first()

        assertEquals(true, value)
    }

    @Test
    fun `fetchMaintenanceStatus cae a datastore cuando falla remote`() = runBlocking {
        every { remoteConfig.fetchAndActivate() } returns Tasks.forException(IllegalStateException("boom"))
        coEvery { dataStore.getMaintenanceMode() } returns flowOf(true)

        val repo = MaintenanceRepository(remoteConfig, dataStore, initRemote = false)

        val value = repo.fetchMaintenanceStatus().first()

        assertEquals(true, value)
        verify(exactly = 0) { remoteConfig.getBoolean(any()) }
        coVerify { dataStore.getMaintenanceMode() }
    }
}
