package com.miskidev.miskiparqueo.feature.maintenance.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], manifest = Config.NONE, application = android.app.Application::class)
class MaintenanceDataStoreTest {

    private lateinit var context: Context
    private lateinit var dataStore: MaintenanceDataStore

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        dataStore = MaintenanceDataStore(context)
        runBlocking {
            context.maintenanceDataStore.edit { prefs ->
                prefs[MaintenanceDataStore.MAINTENANCE_MODE] = false
            }
        }
    }

    @Test
    fun `guarda y recupera maintenance mode`() = runBlocking {
        dataStore.setMaintenanceMode(true)

        val result = dataStore.getMaintenanceMode().first()

        assertEquals(true, result)
    }
}
