package com.example.miskiparqueo.feature.map.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miskiparqueo.feature.map.domain.model.OriginLocationModel
import com.example.miskiparqueo.feature.map.domain.model.ParkingLocationModel
import com.example.miskiparqueo.feature.map.domain.model.RouteInfoModel
import com.example.miskiparqueo.feature.map.domain.usecases.CalculateRouteUseCase
import com.example.miskiparqueo.feature.map.domain.usecases.GetParkingLocationsUseCase
import com.example.miskiparqueo.feature.map.domain.usecases.SearchParkingsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapViewModel(
    private val getParkingLocationsUseCase: GetParkingLocationsUseCase,
    private val searchParkingsUseCase: SearchParkingsUseCase,
    private val calculateRouteUseCase: CalculateRouteUseCase
) : ViewModel() {

    sealed class MapStateUI {
        object Loading : MapStateUI()
        data class Error(val message: String) : MapStateUI()

        // Estado inicial: Mostrando mapa con marcadores, esperando confirmación de origen
        data class ShowingMap(
            val parkings: List<ParkingLocationModel>,
            val originConfirmed: Boolean = false
        ) : MapStateUI()

        // Estado de búsqueda activa
        data class Searching(
            val parkings: List<ParkingLocationModel>,
            val filteredParkings: List<ParkingLocationModel>,
            val query: String
        ) : MapStateUI()

        // Estado cuando se selecciona un parqueo y se calcula la ruta
        data class ParkingSelected(
            val parkings: List<ParkingLocationModel>,
            val routeInfo: RouteInfoModel
        ) : MapStateUI()
    }

    private val _state = MutableStateFlow<MapStateUI>(MapStateUI.Loading)
    val state: StateFlow<MapStateUI> = _state.asStateFlow()

    // Estado del origen (ubicación del usuario)
    private val _origin = MutableStateFlow<OriginLocationModel?>(null)
    val origin: StateFlow<OriginLocationModel?> = _origin.asStateFlow()

    // Lista completa de parqueos (cache)
    private var allParkings: List<ParkingLocationModel> = emptyList()

    init {
        loadParkingLocations()
    }

    // Cargar todos los parqueos
    private fun loadParkingLocations() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = MapStateUI.Loading

            val result = getParkingLocationsUseCase()

            result.fold(
                onSuccess = { parkings ->
                    allParkings = parkings
                    _state.value = MapStateUI.ShowingMap(
                        parkings = parkings,
                        originConfirmed = false
                    )
                },
                onFailure = { error ->
                    _state.value = MapStateUI.Error(error.message ?: "Error desconocido")
                }
            )
        }
    }

    // Confirmar origen (ubicación actual o punto seleccionado en el mapa)
    fun confirmOrigin(latitude: Double, longitude: Double, address: String? = null) {
        _origin.value = OriginLocationModel(latitude, longitude, address)

        // Actualizar estado para indicar que el origen está confirmado
        val currentState = _state.value
        if (currentState is MapStateUI.ShowingMap) {
            _state.value = currentState.copy(originConfirmed = true)
        }
    }

    // Buscar parqueos en tiempo real
    fun searchParkings(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = searchParkingsUseCase(query)

            result.fold(
                onSuccess = { filteredParkings ->
                    _state.value = MapStateUI.Searching(
                        parkings = allParkings,
                        filteredParkings = filteredParkings,
                        query = query
                    )
                },
                onFailure = { error ->
                    _state.value = MapStateUI.Error(error.message ?: "Error en búsqueda")
                }
            )
        }
    }

    // Limpiar búsqueda y volver al estado normal
    fun clearSearch() {
        _state.value = MapStateUI.ShowingMap(
            parkings = allParkings,
            originConfirmed = _origin.value != null
        )
    }

    // Seleccionar un parqueo (desde búsqueda o tap en marcador)
    fun selectParking(parking: ParkingLocationModel) {
        val currentOrigin = _origin.value

        if (currentOrigin == null) {
            _state.value = MapStateUI.Error("Debes confirmar tu origen primero")
            return
        }

        // Calcular ruta
        val routeInfo = calculateRouteUseCase(currentOrigin, parking)

        _state.value = MapStateUI.ParkingSelected(
            parkings = allParkings,
            routeInfo = routeInfo
        )
    }

    // Cancelar selección de parqueo
    fun cancelParkingSelection() {
        _state.value = MapStateUI.ShowingMap(
            parkings = allParkings,
            originConfirmed = true
        )
    }

    // Navegar a ubicación actual del usuario
    fun moveToMyLocation(latitude: Double, longitude: Double) {
        // Este método será llamado desde el Screen para mover la cámara
        // No cambia el estado, solo es un helper
    }

    // Recargar parqueos (por si hay error)
    fun retry() {
        loadParkingLocations()
    }
}