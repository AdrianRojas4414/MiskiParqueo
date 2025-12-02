package com.miskidev.miskiparqueo.feature.maintenance.presentation

import com.miskidev.miskiparqueo.MainDispatcherRule
import com.miskidev.miskiparqueo.feature.maintenance.data.MaintenanceRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], manifest = Config.NONE, application = android.app.Application::class)
class MaintenanceViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `observa estado de mantenimiento`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val flow = MutableStateFlow(true)
        val repo = mockk<MaintenanceRepository> {
            every { observeMaintenanceStatus() } returns flow
            coEvery { fetchMaintenanceStatus() } returns flow
        }
        val vm = MaintenanceViewModel(repo, dispatcher = dispatcher, enablePeriodicFetch = false)

        advanceUntilIdle()

        assertEquals(true, vm.maintenanceMode.value)
    }

    @Test
    fun `refresh actualiza estado`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val flow = MutableStateFlow(false)
        val repo = mockk<MaintenanceRepository> {
            every { observeMaintenanceStatus() } returns flow
            coEvery { fetchMaintenanceStatus() } returns flow
        }
        val vm = MaintenanceViewModel(repo, dispatcher = dispatcher, enablePeriodicFetch = false)

        vm.refreshMaintenanceStatus()
        flow.value = true
        advanceUntilIdle()

        assertEquals(true, vm.maintenanceMode.value)
        io.mockk.coVerify { repo.fetchMaintenanceStatus() }
    }
}
