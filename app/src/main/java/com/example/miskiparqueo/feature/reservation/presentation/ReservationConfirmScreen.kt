package com.example.miskiparqueo.feature.reservation.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.miskiparqueo.feature.reservation.presentation.model.ReservationConfirmArgs
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationConfirmScreen(
    modifier: Modifier = Modifier,
    userId: String,
    parkingId: String,
    dateIso: String,
    entryTimeIso: String,
    exitTimeIso: String,
    totalCost: Double,
    onNavigateBack: () -> Unit,
    onReservationConfirmed: () -> Unit,
    vm: ReservationConfirmViewModel = koinViewModel(
        parameters = {
            parametersOf(
                ReservationConfirmArgs(
                    userId = userId,
                    parkingId = parkingId,
                    dateIso = dateIso,
                    entryTimeIso = entryTimeIso,
                    exitTimeIso = exitTimeIso,
                    totalCost = totalCost
                )
            )
        }
    )
) {
    val state by vm.uiState.collectAsState()

    LaunchedEffect(state.success) {
        if (state.success) {
            vm.consumeSuccess()
            onReservationConfirmed()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Confirmar reserva") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Atr\u00e1s"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            state.isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.errorMessage != null && state.detail == null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(state.errorMessage!!)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = vm::loadDetail) {
                        Text("Reintentar")
                    }
                }
            }

            else -> {
                state.detail?.let { detail ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(24.dp)
                    ) {
                        Text(detail.parking.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text(detail.parking.address, style = MaterialTheme.typography.bodyMedium)

                        Spacer(modifier = Modifier.height(24.dp))

                        SummaryItem(label = "Fecha", value = state.date.toString())
                        SummaryItem(label = "Hora de entrada", value = state.entryTime.toString())
                        SummaryItem(label = "Hora de salida", value = state.exitTime.toString())
                        SummaryItem(label = "Total", value = "Bs ${String.format("%.2f", state.totalCost)}")

                        Spacer(modifier = Modifier.height(32.dp))

                        if (state.errorMessage != null) {
                            Text(
                                text = state.errorMessage!!,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        Button(
                            onClick = vm::confirmReservation,
                            enabled = !state.isConfirming,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (state.isConfirming) {
                                CircularProgressIndicator(modifier = Modifier.height(20.dp))
                            } else {
                                Text("Confirmar reserva")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
    }
}
