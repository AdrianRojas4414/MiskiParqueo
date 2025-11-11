package com.example.miskiparqueo.feature.auth.signup.presentation

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
import com.example.miskiparqueo.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    vm: SignUpViewModel = koinViewModel(),
    onNavigateToLogin: () -> Unit,
    onNavigateToMap: (userId: String) -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(false) }

    val state by vm.state.collectAsState()

    // Manejar navegación en Success
    LaunchedEffect(state) {
        if (state is SignUpViewModel.SignUpStateUI.Success) {
            val userId = (state as SignUpViewModel.SignUpStateUI.Success).user.userId
            onNavigateToMap(userId) // Pasar el userId
            vm.clearState()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Logo
        Image(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = "Logo",
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Título
        Text(
            text = "Crear Cuenta",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Campo Nombre
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("Primer Nombre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = (state as? SignUpViewModel.SignUpStateUI.ValidationError)?.firstNameError != null,
            supportingText = {
                (state as? SignUpViewModel.SignUpStateUI.ValidationError)?.firstNameError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Apellido
        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Apellido") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = (state as? SignUpViewModel.SignUpStateUI.ValidationError)?.lastNameError != null,
            supportingText = {
                (state as? SignUpViewModel.SignUpStateUI.ValidationError)?.lastNameError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Username
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = (state as? SignUpViewModel.SignUpStateUI.ValidationError)?.usernameError != null,
            supportingText = {
                (state as? SignUpViewModel.SignUpStateUI.ValidationError)?.usernameError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = (state as? SignUpViewModel.SignUpStateUI.ValidationError)?.emailError != null,
            supportingText = {
                (state as? SignUpViewModel.SignUpStateUI.ValidationError)?.emailError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            }
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
                        contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            },
            isError = (state as? SignUpViewModel.SignUpStateUI.ValidationError)?.passwordError != null,
            supportingText = {
                (state as? SignUpViewModel.SignUpStateUI.ValidationError)?.passwordError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Checkbox Términos
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = termsAccepted,
                onCheckedChange = { termsAccepted = it }
            )
            Text(
                text = "Estoy de acuerdo con los términos y condiciones",
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón Registrarse
        Button(
            onClick = {
                if (termsAccepted) {
                    vm.signUp(firstName, lastName, username, email, password)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = termsAccepted && state !is SignUpViewModel.SignUpStateUI.Loading
        ) {
            Text("Registrarse", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Estados de UI
        when (val st = state) {
            is SignUpViewModel.SignUpStateUI.Loading -> {
                CircularProgressIndicator()
            }
            is SignUpViewModel.SignUpStateUI.Error -> {
                Text(
                    text = st.message,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
            is SignUpViewModel.SignUpStateUI.Success -> {
                // La navegación se maneja en LaunchedEffect
            }
            else -> {}
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Link a Login
        Text(
            text = "¿Ya tienes una cuenta? Ingresa",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { onNavigateToLogin() }
        )
    }
}