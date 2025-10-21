package com.example.miskiparqueo.feature.map.presentation

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
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

@OptIn(ExperimentalMaterial3Api::class)
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

    // 2. ESTADO DE LA HOJA INFERIOR (Bottom Sheet)
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()

    // 3. ESTRUCTURA DE LA PANTALLA
    BottomSheetScaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = bottomSheetScaffoldState,
        // Contenido de la hoja inferior (Mockup 2)
        sheetContent = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Name: <ParkingName>", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text("Tiempo de llegada: <00:00>", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(16.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { /* TODO: vm.onCloseBottomSheet() */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cerrar")
                    }
                    Button(
                        onClick = { /* TODO: vm.onReserveClick() */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reserva")
                    }
                }
            }
        },
        sheetPeekHeight = 0.dp // <-- La hoja estará oculta por defecto
    ) { scaffoldPadding ->

        // 4. CONTENIDO PRINCIPAL (Mapa y Botones)
        // Usamos un Box para apilar elementos uno encima del otro
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding), // <-- Aplicamos el padding del scaffold
            contentAlignment = Alignment.Center
        ) {

            if (hasLocationPermission) {
                val state by vm.state.collectAsState()

                // Cámara en Cochabamba (Sin cambios)
                val cochabamba = LatLng(-17.3895, -66.1569)
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(cochabamba, 14f)
                }

                // MAPA
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    // Deshabilitamos el botón de "Mi Ubicación" de Google
                    uiSettings = MapUiSettings(myLocationButtonEnabled = false),
                    // Habilitamos el "punto azul" de la ubicación real
                    properties = MapProperties(isMyLocationEnabled = true)
                ) {
                    // Lógica de Marcadores (Sin cambios por ahora)
                    if (state is MapViewModel.MapStateUI.Success) {
                        (state as MapViewModel.MapStateUI.Success).locations.forEach { parking ->
                            Marker(
                                state = MarkerState(position = LatLng(parking.latitude, parking.longitude)),
                                title = parking.name
                            )
                        }
                    }
                }

                // Estados de Carga/Error (Sin cambios)
                when (val currentState = state) {
                    is MapViewModel.MapStateUI.Loading -> CircularProgressIndicator()
                    is MapViewModel.MapStateUI.Error -> Text(currentState.message, color = Color.Red)
                    else -> {}
                }

                // --- 5. BOTONES SUPERPUESTOS (¡NUEVO!) ---

                // Botón de Perfil (Esquina superior derecha)
                IconButton(
                    onClick = { /* TODO: Navegar al perfil */ },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 16.dp, end = 16.dp)
                        .size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile Options",
                        modifier = Modifier.fillMaxSize(),
                        tint = Color.DarkGray
                    )
                }

                // Pin de Origen (Centro)
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Pin de Origen",
                    tint = Color.Red, // El pin rojo del mockup
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center)
                )

                // Botón "Mi Ubicación" (Esquina inferior derecha)
                FloatingActionButton(
                    onClick = { /* TODO: vm.onMyLocationButtonClick() */ },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Navigation, // <-- Icono de navegación
                        contentDescription = "Mi Ubicación"
                    )
                }

            } else {
                // El usuario no ha dado permiso (Sin cambios)
                Text(
                    text = "Se necesitan permisos de ubicación para mostrar el mapa.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}