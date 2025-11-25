package com.miskidev.miskiparqueo.feature.auth.login.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miskidev.miskiparqueo.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    vm: LoginViewModel = koinViewModel(),
    onNavigateToSignUp: () -> Unit,
    onNavigateToMap: (userId: String) -> Unit
) {
    var credential by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    //var rememberMe by remember { mutableStateOf(false) }

    val state by vm.state.collectAsState()

    LaunchedEffect(state) {
        if (state is LoginViewModel.LoginStateUI.Success) {
            val userId = (state as LoginViewModel.LoginStateUI.Success).user.userId
            onNavigateToMap(userId) // Pasar el userId
            vm.clearState()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = "Logo",
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Título de la App
        Text(
            text = "BIENVENIDO",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Campo Email o Username
        OutlinedTextField(
            value = credential,
            onValueChange = { credential = it },
            label = { Text("Email o Username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Ocultar" else "Mostrar"
                    )
                }
            }
        )

        // Mostrar error de validación debajo del campo correspondiente si es necesario
        if (state is LoginViewModel.LoginStateUI.ValidationError) {
            Text(
                text = (state as LoginViewModel.LoginStateUI.ValidationError).message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                fontSize = 12.sp
            )
        }


        Spacer(modifier = Modifier.height(16.dp))

        // Checkbox "Recuérdame"
        /*Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = { rememberMe = it }
            )
            Text(
                text = "Recuérdame",
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }*/

        Spacer(modifier = Modifier.height(24.dp))

        // Botón Ingresar
        Button(
            onClick = { vm.login(credential, password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = state !is LoginViewModel.LoginStateUI.Loading,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD149)) // Color amarillo del mockup
        ) {
            Text("Ingresa", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Estados de UI (Loading o Error general)
        when (val st = state) {
            is LoginViewModel.LoginStateUI.Loading -> {
                CircularProgressIndicator()
            }
            is LoginViewModel.LoginStateUI.Error -> {
                Text(
                    text = st.message,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
            else -> {}
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Link a Registro
        Text(
            text = "¿Aún no tienes una cuenta? Regístrate",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { onNavigateToSignUp() }
        )
    }
}