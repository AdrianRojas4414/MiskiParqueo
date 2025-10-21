package com.example.miskiparqueo.feature.map.presentation

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import org.koin.androidx.compose.koinViewModel

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    vm: MapViewModel = koinViewModel()
) {
    // 1. MANEJO DE PERMISOS
    var hasLocationPermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            hasLocationPermission = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)
        }
    )

    // Solicitar permisos en cuanto la pantalla se compone
    LaunchedEffect(key1 = true) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    // 2. LÓGICA DE LA PANTALLA
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (hasLocationPermission) {
            // El usuario dio permiso, mostramos el mapa
            val state by vm.state.collectAsState()

            // Centramos la cámara en Cochabamba
            val cochabamba = LatLng(-17.3895, -66.1569)
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(cochabamba, 14f) // Zoom nivel ciudad
            }

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                // Habilitamos el botón de "Mi Ubicación"
                uiSettings = MapUiSettings(myLocationButtonEnabled = true),
                properties = MapProperties(isMyLocationEnabled = true)
            ) {
                // Dibujamos los marcadores cuando el estado sea Success
                if (state is MapViewModel.MapStateUI.Success) {
                    (state as MapViewModel.MapStateUI.Success).locations.forEach { parking ->
                        Marker(
                            state = MarkerState(position = LatLng(parking.latitude, parking.longitude)),
                            title = parking.name,
                            snippet = "Toca para ver detalles" // A futuro
                        )
                    }
                }
            }

            // Manejamos los estados de Loading y Error
            when (val currentState = state) {
                is MapViewModel.MapStateUI.Loading -> {
                    CircularProgressIndicator()
                }
                is MapViewModel.MapStateUI.Error -> {
                    Text(
                        text = currentState.message,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.Center),
                        textAlign = TextAlign.Center
                    )
                }
                is MapViewModel.MapStateUI.Success -> {
                    // Los marcadores ya están dibujados en el mapa
                }
            }
        } else {
            // El usuario no ha dado permiso
            Text(
                text = "Se necesitan permisos de ubicación para mostrar el mapa.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}