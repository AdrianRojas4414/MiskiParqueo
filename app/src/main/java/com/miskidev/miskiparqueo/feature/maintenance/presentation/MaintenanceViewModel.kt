package com.miskidev.miskiparqueo.feature.maintenance.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miskidev.miskiparqueo.feature.maintenance.data.MaintenanceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MaintenanceViewModel(
    private val repository: MaintenanceRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val enablePeriodicFetch: Boolean = true
): ViewModel() {
    private val _maintenanceMode = MutableStateFlow(false)
    val maintenanceMode: StateFlow<Boolean> = _maintenanceMode.asStateFlow()

    init {
        observeMaintenanceStatus()
        if (enablePeriodicFetch) startPeriodicFetch()
    }

    private fun observeMaintenanceStatus() {
        viewModelScope.launch {
            repository.observeMaintenanceStatus().collect { isMaintenance ->
                _maintenanceMode.value = isMaintenance
                Log.d("MaintenanceViewModel", "Maintenance status observado: $isMaintenance")
            }
        }
    }

    private fun startPeriodicFetch() {
        viewModelScope.launch(dispatcher) {
            while (enablePeriodicFetch) {
                try {
                    fetchMaintenanceStatus()
                    delay(5000) // Fetch cada 5 segundos
                } catch (e: Exception) {
                    Log.e("MaintenanceViewModel", "Error en periodic fetch", e)
                    delay(60000)
                }
            }
        }
    }

    private suspend fun fetchMaintenanceStatus() {
        try {
            repository.fetchMaintenanceStatus().collect { isMaintenance ->
                _maintenanceMode.value = isMaintenance
                Log.d("MaintenanceViewModel", "Fetch periódico completado: $isMaintenance")
            }
        } catch (e: Exception) {
            Log.e("MaintenanceViewModel", "Error en fetchMaintenanceStatus", e)
        }
    }

    fun refreshMaintenanceStatus() {
        viewModelScope.launch(dispatcher) {
            fetchMaintenanceStatus()
        }
    }
}
