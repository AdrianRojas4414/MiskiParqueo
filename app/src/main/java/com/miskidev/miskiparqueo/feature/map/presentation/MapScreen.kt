package com.miskidev.miskiparqueo.feature.map.presentation

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars

@OptIn(FlowPreview::class, ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    vm: MapViewModel = koinViewModel(),
    onNavigateToProfile: () -> Unit,
    onNavigateToReservations: () -> Unit,
    onNavigateToReservation: (String) -> Unit
) {
    // 1. PERMISOS DE UBICACIÓN
    var hasLocationPermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            hasLocationPermission = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)
        }
    )

    LaunchedEffect(key1 = true) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    // 2. ESTADOS
    val state by vm.state.collectAsState()
    val origin by vm.origin.collectAsState()
    var destinationQuery by remember { mutableStateOf("") }
    var isSelectingOrigin by remember { mutableStateOf(false) }
    var shouldSearch by remember { mutableStateOf(true) }
    var showProfileMenu by remember { mutableStateOf(false) }

    // Cliente de ubicación
    val context = androidx.compose.ui.platform.LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // 3. DEBOUNCE para búsqueda
    val searchFlow = remember { MutableStateFlow("") }

    LaunchedEffect(destinationQuery) {
        searchFlow.value = destinationQuery
    }

    LaunchedEffect(Unit) {
        searchFlow
            .debounce(300)
            .collect { query ->
                if (query.isNotEmpty() && origin != null && shouldSearch) {
                    vm.searchParkings(query)
                } else if (query.isEmpty() && origin != null) {
                    vm.clearSearch()
                }
            }
    }

    // 4. CÁMARA Y MAPA
    val cochabamba = LatLng(-17.3895, -66.1569)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(cochabamba, 14f)
    }
    val coroutineScope = rememberCoroutineScope()

    // Obtener ubicación GPS automáticamente cuando se otorgan permisos
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission && origin == null) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val userLocation = LatLng(location.latitude, location.longitude)

                    // Confirmar origen automáticamente con la ubicación GPS
                    vm.confirmOrigin(
                        location.latitude,
                        location.longitude,
                        "Tu ubicación actual"
                    )

                    // Centrar mapa en la ubicación del usuario
                    coroutineScope.launch {
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newLatLngZoom(userLocation, 15f),
                            durationMs = 1000
                        )
                    }
                }
            }
        }
    }

    // 5. BOTTOM SHEET
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()

    LaunchedEffect(state) {
        if (state is MapViewModel.MapStateUI.ParkingSelected) {
            bottomSheetScaffoldState.bottomSheetState.expand()
        } else {
            bottomSheetScaffoldState.bottomSheetState.partialExpand()
        }
    }

    BottomSheetScaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = bottomSheetScaffoldState,
        sheetPeekHeight = if (state is MapViewModel.MapStateUI.ParkingSelected) 200.dp else 0.dp,
        sheetContent = {
            when (val currentState = state) {
                is MapViewModel.MapStateUI.ParkingSelected -> {
                    ParkingDetailsBottomSheet(
                        routeInfo = currentState.routeInfo,
                        onCancel = {
                            vm.cancelParkingSelection()
                            destinationQuery = ""
                            shouldSearch = true
                        },
                        onReserve = {
                            onNavigateToReservation(currentState.routeInfo.destination.id)
                        }
                    )
                }
                else -> {
                    Spacer(modifier = Modifier.height(1.dp))
                }
            }
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (hasLocationPermission) {
                // MAPA
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(
                        myLocationButtonEnabled = false,
                        zoomControlsEnabled = false
                    ),
                    properties = MapProperties(isMyLocationEnabled = true)
                ) {
                    // MARCADORES DE PARQUEOS
                    when (val currentState = state) {
                        is MapViewModel.MapStateUI.ShowingMap,
                        is MapViewModel.MapStateUI.Searching -> {
                            val parkings = when (currentState) {
                                is MapViewModel.MapStateUI.ShowingMap -> currentState.parkings
                                is MapViewModel.MapStateUI.Searching -> currentState.parkings
                                else -> emptyList()
                            }

                            parkings.forEach { parking ->
                                Marker(
                                    state = MarkerState(position = LatLng(parking.latitude, parking.longitude)),
                                    title = parking.name,
                                    snippet = "${parking.availableSpots} cupos disponibles",
                                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                                    onClick = {
                                        if (currentState is MapViewModel.MapStateUI.ShowingMap &&
                                            currentState.originConfirmed) {
                                            vm.selectParking(parking)
                                            destinationQuery = ""
                                        }
                                        true
                                    }
                                )
                            }
                        }
                        is MapViewModel.MapStateUI.ParkingSelected -> {
                            // Marcador del DESTINO (azul)
                            Marker(
                                state = MarkerState(position = LatLng(
                                    currentState.routeInfo.destination.latitude,
                                    currentState.routeInfo.destination.longitude
                                )),
                                title = currentState.routeInfo.destination.name,
                                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                            )

                            // RUTA (Línea recta)
                            Polyline(
                                points = listOf(
                                    LatLng(currentState.routeInfo.origin.latitude, currentState.routeInfo.origin.longitude),
                                    LatLng(currentState.routeInfo.destination.latitude, currentState.routeInfo.destination.longitude)
                                ),
                                color = Color(0xFFFF9800),
                                width = 10f
                            )
                        }
                        else -> {}
                    }

                    // Marcador del ORIGEN (rojo)
                    if (origin != null && !isSelectingOrigin) {
                        Marker(
                            state = MarkerState(position = LatLng(
                                origin!!.latitude,
                                origin!!.longitude
                            )),
                            title = "Tu ubicación",
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                        )
                    }
                }

                // ESTADOS DE CARGA/ERROR
                when (val currentState = state) {
                    is MapViewModel.MapStateUI.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    is MapViewModel.MapStateUI.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = currentState.message,
                                    color = Color.Red,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(16.dp)
                                )
                                Button(onClick = { vm.retry() }) {
                                    Text("Reintentar")
                                }
                            }
                        }
                    }
                    else -> {}
                }

                // BARRAS DE ORIGEN Y DESTINO
                if (origin != null && !isSelectingOrigin) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 80.dp, start = 16.dp, end = 16.dp)
                            .fillMaxWidth()
                    ) {
                        OriginBar(
                            address = origin?.address ?: "Tu ubicación actual",
                            onChangeOriginClick = {
                                isSelectingOrigin = true
                                destinationQuery = ""
                                shouldSearch = true
                                vm.cancelParkingSelection()
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        DestinationSearchBar(
                            query = destinationQuery,
                            onQueryChange = {
                                destinationQuery = it
                                shouldSearch = true
                            },
                            onClearClick = {
                                destinationQuery = ""
                                shouldSearch = true
                                vm.clearSearch()
                            }
                        )
                    }
                }

                // LISTA DE SUGERENCIAS
                if (state is MapViewModel.MapStateUI.Searching && destinationQuery.isNotEmpty()) {
                    SearchSuggestions(
                        parkings = (state as MapViewModel.MapStateUI.Searching).filteredParkings,
                        onParkingClick = { parking ->
                            shouldSearch = false
                            vm.selectParking(parking)
                            destinationQuery = parking.name
                        },
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 200.dp, start = 16.dp, end = 16.dp)
                    )
                }

                // PIN DE ORIGEN (Centro) - Selección origen manual
                if (origin == null || isSelectingOrigin) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Pin de Origen",
                        tint = Color.Red,
                        modifier = Modifier
                            .size(48.dp)
                            .align(Alignment.Center)
                    )
                }

                // BOTÓN "CONFIRMAR ORIGEN"
                if (isSelectingOrigin) {
                    Button(
                        onClick = {
                            val centerPosition = cameraPositionState.position.target
                            vm.confirmOrigin(
                                centerPosition.latitude,
                                centerPosition.longitude,
                                "Ubicación seleccionada"
                            )
                            isSelectingOrigin = false
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp)
                            .fillMaxWidth(0.8f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFEB3B)
                        )
                    ) {
                        Text(
                            text = "Confirmar Origen",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 16.dp, end = 16.dp)
                ) {
                    IconButton(
                        onClick = { showProfileMenu = true },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Perfil",
                            modifier = Modifier.fillMaxSize(),
                            tint = Color.DarkGray
                        )
                    }
                    DropdownMenu(
                        expanded = showProfileMenu,
                        onDismissRequest = { showProfileMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Perfil") },
                            onClick = {
                                showProfileMenu = false
                                onNavigateToProfile()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Reservas") },
                            onClick = {
                                showProfileMenu = false
                                onNavigateToReservations()
                            }
                        )
                    }
                }

                // BOTÓN "CENTRAR UBICACIÓN GPS"
                FloatingActionButton(
                    onClick = {
                        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                            if (location != null) {
                                val userLocation = LatLng(location.latitude, location.longitude)
                                coroutineScope.launch {
                                    cameraPositionState.animate(
                                        update = CameraUpdateFactory.newLatLngZoom(userLocation, 16f),
                                        durationMs = 1000
                                    )
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Ir a mi ubicación GPS"
                    )
                }

            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
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
}

// COMPOSABLE: Barra de Origen (Roja)
@Composable
fun OriginBar(
    address: String,
    onChangeOriginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onChangeOriginClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Origen",
                tint = Color.Red,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = address,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// COMPOSABLE: Barra de búsqueda de destino
@Composable
fun DestinationSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar",
                tint = Color(0xFF2196F3), // Azul
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                decorationBox = { innerTextField ->
                    if (query.isEmpty()) {
                        Text(
                            text = "Selecciona un parqueo",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                    innerTextField()
                }
            )
            if (query.isNotEmpty()) {
                IconButton(onClick = onClearClick) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Limpiar",
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}

// COMPOSABLE: Lista de sugerencias
@Composable
fun SearchSuggestions(
    parkings: List<com.miskidev.miskiparqueo.feature.map.domain.model.ParkingLocationModel>,
    onParkingClick: (com.miskidev.miskiparqueo.feature.map.domain.model.ParkingLocationModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 300.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        LazyColumn {
            items(parkings) { parking ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onParkingClick(parking) }
                        .padding(16.dp)
                ) {
                    Text(
                        text = parking.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = parking.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                if (parking != parkings.last()) {
                    HorizontalDivider()
                }
            }
        }
    }
}

// COMPOSABLE: Detalles del parqueo en Bottom Sheet
@Composable
fun ParkingDetailsBottomSheet(
    routeInfo: com.miskidev.miskiparqueo.feature.map.domain.model.RouteInfoModel,
    onCancel: () -> Unit,
    onReserve: () -> Unit
) {
    // Obtener el padding de la barra de navegación del sistema
    val navigationBarPadding = WindowInsets.navigationBars.asPaddingValues()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .padding(top = 24.dp)
            // CLAVE: Añadir padding bottom para la barra de navegación
            .padding(bottom = navigationBarPadding.calculateBottomPadding()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = routeInfo.destination.name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = routeInfo.destination.address,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            InfoItem(
                label = "Tiempo de llegada",
                value = "${routeInfo.estimatedTimeMinutes} min"
            )
            InfoItem(
                label = "Tarifa por hora",
                value = "${routeInfo.destination.pricePerHour} Bs"
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            InfoItem(
                label = "Cupos disponibles",
                value = "${routeInfo.destination.availableSpots}/${routeInfo.destination.totalSpots}"
            )
            InfoItem(
                label = "Horario",
                value = routeInfo.destination.operatingHours
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }
            Button(
                onClick = onReserve,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFEB3B)
                )
            ) {
                Text("Reservar", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
        // Espacio adicional de seguridad
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun InfoItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}
