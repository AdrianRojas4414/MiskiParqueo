package com.miskidev.miskiparqueo.feature.profile.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    vm: ProfileViewModel = koinViewModel(),
    onNavigateToChangePassword: () -> Unit,
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit
) {
    val user by vm.userState.collectAsState()
    val uiState by vm.uiState.collectAsState()

    // Estado para el diálogo de confirmación
    var showLogoutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
            }
            Text(
                text = "MI PERFIL",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Campo First Name
        OutlinedTextField(
            value = user?.firstName?.value ?: "",
            onValueChange = { vm.onFirstNameChanged(it) },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            trailingIcon = { Icon(Icons.Default.Edit, contentDescription = "Editar") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Last Name
        OutlinedTextField(
            value = user?.lastName?.value ?: "",
            onValueChange = { vm.onLastNameChanged(it) },
            label = { Text("Apellido") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            trailingIcon = { Icon(Icons.Default.Edit, contentDescription = "Editar") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Username
        OutlinedTextField(
            value = user?.username?.value ?: "",
            onValueChange = { vm.onUsernameChanged(it) },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            trailingIcon = { Icon(Icons.Default.Edit, contentDescription = "Editar") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Email
        OutlinedTextField(
            value = user?.email?.value ?: "",
            onValueChange = { vm.onEmailChanged(it) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            trailingIcon = { Icon(Icons.Default.Edit, contentDescription = "Editar") }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botón Guardar Cambios
        Button(
            onClick = { vm.saveChanges() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = uiState !is ProfileViewModel.ProfileUIState.Loading,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD149))
        ) {
            Text("Guardar Cambios", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón Cambiar Contraseña
        OutlinedButton(
            onClick = onNavigateToChangePassword,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Cambiar Contraseña", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // NUEVO: Botón Cerrar Sesión
        OutlinedButton(
            onClick = { showLogoutDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = "Cerrar sesión",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cerrar Sesión", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Estados de UI (Loading, Error o Success)
        when (val state = uiState) {
            is ProfileViewModel.ProfileUIState.Loading -> {
                CircularProgressIndicator()
            }
            is ProfileViewModel.ProfileUIState.Error -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
            is ProfileViewModel.ProfileUIState.Success -> {
                Text(
                    text = state.message,
                    color = Color(0xFF00C853),
                    textAlign = TextAlign.Center
                )
            }
            else -> {}
        }
    }

    // NUEVO: Diálogo de confirmación de cierre de sesión
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Estás seguro de que deseas cerrar sesión?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text("Sí, cerrar sesión", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}