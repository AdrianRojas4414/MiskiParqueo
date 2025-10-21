package com.example.miskiparqueo.feature.map.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miskiparqueo.feature.map.domain.model.ParkingLocationModel
import com.example.miskiparqueo.feature.map.domain.usecase.GetParkingLocationsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapViewModel(
    private val getParkingLocationsUseCase: GetParkingLocationsUseCase
) : ViewModel() {

    sealed class MapStateUI {
        object Loading : MapStateUI()
        data class Error(val message: String) : MapStateUI()
        data class Success(val locations: List<ParkingLocationModel>) : MapStateUI()
    }

    private val _state = MutableStateFlow<MapStateUI>(MapStateUI.Loading)
    val state: StateFlow<MapStateUI> = _state.asStateFlow()

    init {
        loadParkingLocations()
    }

    fun loadParkingLocations() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = MapStateUI.Loading

            val result = getParkingLocationsUseCase()

            result.fold(
                onSuccess = { parkings ->
                    _state.value = MapStateUI.Success(parkings)
                },
                onFailure = { error ->
                    _state.value = MapStateUI.Error(error.message ?: "Error desconocido")
                }
            )
        }
    }
}