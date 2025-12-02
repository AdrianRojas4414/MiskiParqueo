package com.miskidev.miskiparqueo.integration

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.tasks.Tasks
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.miskidev.miskiparqueo.MainDispatcherRule
import com.miskidev.miskiparqueo.feature.maintenance.data.MaintenanceDataStore
import com.miskidev.miskiparqueo.feature.maintenance.data.MaintenanceRepository
import com.miskidev.miskiparqueo.feature.maintenance.data.maintenanceDataStore
import com.miskidev.miskiparqueo.feature.maintenance.presentation.MaintenanceViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], manifest = Config.NONE, application = android.app.Application::class)
class MaintenanceIntegrationTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var context: Context
    private lateinit var dataStore: MaintenanceDataStore

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        dataStore = MaintenanceDataStore(context)
        // estado inicial en falso para verificar propagacion
        runBlocking {
            context.maintenanceDataStore.edit { prefs ->
                prefs[MaintenanceDataStore.MAINTENANCE_MODE] = false
            }
        }
    }

    @Test
    fun `refresh propaga remote config a datastore y viewmodel`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val remoteConfig = mockk<FirebaseRemoteConfig>(relaxed = true)
        every { remoteConfig.fetchAndActivate() } returns Tasks.forResult(true)
        every { remoteConfig.getBoolean("maintenance_mode") } returns true

        val repository = MaintenanceRepository(remoteConfig, dataStore, initRemote = false)
        val viewModel = MaintenanceViewModel(repository, dispatcher = dispatcher, enablePeriodicFetch = false)

        viewModel.refreshMaintenanceStatus()
        advanceUntilIdle()

        val mode = viewModel.maintenanceMode.first { it }
        val stored = dataStore.getMaintenanceMode().first()
        assertTrue(mode)
        assertTrue(stored)
    }

    @Test
    fun `estado inicial viene de datastore`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val remoteConfig = mockk<FirebaseRemoteConfig>(relaxed = true)
        every { remoteConfig.fetchAndActivate() } returns Tasks.forResult(true)
        every { remoteConfig.getBoolean("maintenance_mode") } returns false

        val repository = MaintenanceRepository(remoteConfig, dataStore, initRemote = false)
        val viewModel = MaintenanceViewModel(repository, dispatcher = dispatcher, enablePeriodicFetch = false)

        advanceUntilIdle()

        val current = viewModel.maintenanceMode.first()
        val stored = dataStore.getMaintenanceMode().first()
        assertTrue(!current)
        assertTrue(!stored)
    }

    @Test
    fun `fallo de remote mantiene valor previo`() = runTest {
        runBlocking {
            context.maintenanceDataStore.edit { prefs ->
                prefs[MaintenanceDataStore.MAINTENANCE_MODE] = true
            }
        }
        val dispatcher = StandardTestDispatcher(testScheduler)
        val remoteConfig = mockk<FirebaseRemoteConfig>(relaxed = true)
        every { remoteConfig.fetchAndActivate() } returns Tasks.forException(IllegalStateException("boom"))
        every { remoteConfig.getBoolean("maintenance_mode") } returns false

        val repository = MaintenanceRepository(remoteConfig, dataStore, initRemote = false)
        val viewModel = MaintenanceViewModel(repository, dispatcher = dispatcher, enablePeriodicFetch = false)

        viewModel.refreshMaintenanceStatus()
        advanceUntilIdle()

        val mode = viewModel.maintenanceMode.first()
        val stored = dataStore.getMaintenanceMode().first()
        assertTrue(mode)
        assertTrue(stored)
    }
}
