package com.example.miskiparqueo.feature.reservation.presentation

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.miskiparqueo.R
import com.example.miskiparqueo.feature.reservation.presentation.ReservationViewModel.ReservationUiState
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationScreen(
    modifier: Modifier = Modifier,
    userId: String,
    parkingId: String,
    onNavigateBack: () -> Unit,
    onNavigateToConfirm: (String, String, String, String, String, Double) -> Unit,
    vm: ReservationViewModel = koinViewModel(parameters = { parametersOf(parkingId) })
) {
    val state by vm.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detalle de reserva") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Atr\u00e1s"
                        )
                    }
                }
            )
        },
        bottomBar = {
            ReservationBottomBar(
                state = state,
                onReserve = {
                    onNavigateToConfirm(
                        userId,
                        parkingId,
                        state.selectedDate.toString(),
                        state.entryTime.toString(),
                        state.exitTime.toString(),
                        state.totalCost
                    )
                }
            )
        }
    ) { innerPadding ->
        val errorMessage = state.errorMessage
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            errorMessage != null -> {
                ErrorState(
                    message = errorMessage,
                    onRetry = vm::reload,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            state.detail != null -> {
                ReservationContent(
                    state = state,
                    modifier = Modifier
                        .padding(innerPadding)
                        .verticalScroll(scrollState)
                        .padding(16.dp),
                    onPickDate = {
                        showDatePickerDialog(
                            context = context,
                            currentDate = state.selectedDate,
                            onDateSelected = vm::onDateSelected
                        )
                    },
                    onPickEntryTime = {
                        showTimePickerDialog(
                            context = context,
                            currentTime = state.entryTime,
                            onTimeSelected = vm::onEntryTimeSelected
                        )
                    },
                    onPickExitTime = {
                        showTimePickerDialog(
                            context = context,
                            currentTime = state.exitTime,
                            onTimeSelected = vm::onExitTimeSelected
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Reintentar")
        }
    }
}

@Composable
private fun ReservationContent(
    state: ReservationUiState,
    modifier: Modifier = Modifier,
    onPickDate: () -> Unit,
    onPickEntryTime: () -> Unit,
    onPickExitTime: () -> Unit
) {
    val detail = state.detail ?: return
    val locale = remember { Locale("es", "ES") }
    val dateFormatter = remember(locale) {
        DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", locale)
    }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    Column(modifier = modifier) {
        val context = LocalContext.current
        val imageResId = remember(detail.imageName) {
            context.resources.getIdentifier(detail.imageName, "drawable", context.packageName)
        }.takeIf { it != 0 } ?: R.drawable.img_parking_default

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = detail.parking.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = detail.parking.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = detail.description,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(12.dp))

        InfoRow(
            icon = Icons.Default.LocationOn,
            title = detail.parking.address,
            subtitle = "Horario: ${detail.parking.operatingHours}"
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Tarifa por hora", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text(
                        text = "Bs ${String.format(locale, "%.2f", detail.parking.pricePerHour)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Cupos", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text(
                        text = "${detail.parking.availableSpots}/${detail.parking.totalSpots}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Comodidades",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        AmenitiesGrid(amenities = detail.amenities)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Selecciona tu horario",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        SelectorCard(
            title = "Fecha",
            value = state.selectedDate.format(dateFormatter),
            icon = Icons.Default.CalendarMonth,
            onClick = onPickDate
        )
        Spacer(modifier = Modifier.height(12.dp))
        SelectorCard(
            title = "Hora de entrada",
            value = state.entryTime.format(timeFormatter),
            icon = Icons.Default.AccessTime,
            onClick = onPickEntryTime
        )
        Spacer(modifier = Modifier.height(12.dp))
        SelectorCard(
            title = "Hora de salida",
            value = state.exitTime.format(timeFormatter),
            icon = Icons.Default.AccessTime,
            onClick = onPickExitTime
        )

        state.validationError?.let { validationMessage ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = validationMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun AmenitiesGrid(amenities: List<String>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        amenities.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { amenity ->
                    AmenityChip(text = amenity)
                }
            }
        }
    }
}

@Composable
private fun AmenityChip(text: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .padding(end = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}

@Composable
private fun SelectorCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(title, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
            Icon(imageVector = icon, contentDescription = title)
        }
    }
}

@Composable
private fun ReservationBottomBar(
    state: ReservationUiState,
    onReserve: () -> Unit
) {
    if (state.detail == null || state.isLoading) return

    val navigationBarPadding = WindowInsets.navigationBars.asPaddingValues()
    val hasSpots = state.hasAvailableSpots
    val hasTimeError = state.validationError?.contains("hora de salida", ignoreCase = true) == true

    Surface(shadowElevation = 12.dp) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(top = 16.dp)
                .padding(bottom = navigationBarPadding.calculateBottomPadding())
        ) {
            // Mostrar advertencia si no hay cupos
            if (!hasSpots) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Sin cupos",
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Sin cupos disponibles",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD32F2F)
                            )
                            Text(
                                text = "Este parqueo ha alcanzado su capacidad máxima (0/${state.detail.parking.totalSpots})",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF5F0000)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Costo estimado", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text(
                        text = "Bs ${String.format(Locale.getDefault(), "%.2f", state.totalCost)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (hasSpots) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Tarifa x hora: Bs ${String.format(Locale.getDefault(), "%.2f", state.detail.parking.pricePerHour)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    // NUEVO: Mostrar cupos disponibles con color
                    val spotsColor = when {
                        state.detail.parking.availableSpots == 0 -> Color(0xFFD32F2F)
                        state.detail.parking.availableSpots <= 5 -> Color(0xFFFF9800)
                        else -> Color(0xFF4CAF50)
                    }
                    Text(
                        text = "Cupos: ${state.detail.parking.availableSpots}/${state.detail.parking.totalSpots}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = spotsColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onReserve,
                enabled = hasSpots && !hasTimeError,  // NUEVO: deshabilitar si no hay cupos o hay error de horario
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (hasSpots) MaterialTheme.colorScheme.primary else Color.Gray,
                    disabledContainerColor = Color(0xFFE0E0E0)
                )
            ) {
                Text(
                    text = if (!hasSpots) "Sin cupos disponibles" else "Reservar",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

private fun showDatePickerDialog(
    context: android.content.Context,
    currentDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val dialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            onDateSelected(LocalDate.of(year, month + 1, dayOfMonth))
        },
        currentDate.year,
        currentDate.monthValue - 1,
        currentDate.dayOfMonth
    )
    dialog.datePicker.minDate = System.currentTimeMillis()
    dialog.show()
}

private fun showTimePickerDialog(
    context: android.content.Context,
    currentTime: LocalTime,
    onTimeSelected: (LocalTime) -> Unit
) {
    TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            onTimeSelected(LocalTime.of(hourOfDay, minute))
        },
        currentTime.hour,
        currentTime.minute,
        true
    ).show()
}
