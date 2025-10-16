package com.example.miskiparqueo.feature.auth.signup.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miskiparqueo.feature.auth.signup.domain.model.UserSignUpModel
import com.example.miskiparqueo.feature.auth.signup.domain.model.vo.Email
import com.example.miskiparqueo.feature.auth.signup.domain.model.vo.FirstName
import com.example.miskiparqueo.feature.auth.signup.domain.model.vo.LastName
import com.example.miskiparqueo.feature.auth.signup.domain.model.vo.Password
import com.example.miskiparqueo.feature.auth.signup.domain.model.vo.Username
import com.example.miskiparqueo.feature.auth.signup.domain.usecases.SignUpUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val signUpUseCase: SignUpUseCase
): ViewModel() {

    sealed class SignUpStateUI {
        object Init: SignUpStateUI()
        object Loading: SignUpStateUI()
        data class Error(val message: String): SignUpStateUI()
        data class Success(val user: UserSignUpModel): SignUpStateUI()
        data class ValidationError(
            val firstNameError: String? = null,
            val lastNameError: String? = null,
            val usernameError: String? = null,
            val emailError: String? = null,
            val passwordError: String? = null
        ): SignUpStateUI()
    }

    private val _state = MutableStateFlow<SignUpStateUI>(SignUpStateUI.Init)
    val state: StateFlow<SignUpStateUI> = _state.asStateFlow()

    fun signUp(
        firstNameRaw: String,
        lastNameRaw: String,
        usernameRaw: String,
        emailRaw: String,
        passwordRaw: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = SignUpStateUI.Loading

            try {
                val firstName = FirstName.create(firstNameRaw)
                val lastName = LastName.create(lastNameRaw)
                val username = Username.create(usernameRaw)
                val email = Email.create(emailRaw)
                val password = Password.create(passwordRaw)

                val result = signUpUseCase.invoke(firstName, lastName, username, email, password)

                result.fold(
                    onSuccess = { user ->
                        _state.value = SignUpStateUI.Success(user)
                    },
                    onFailure = { error ->
                        _state.value = SignUpStateUI.Error(message = error.message ?: "Error desconocido")
                    }
                )
            } catch (e: IllegalArgumentException) {
                val errorMessage = e.message ?: "Error de validación"
                _state.value = assignErrorToField(errorMessage)
            }
        }
    }

    private fun assignErrorToField(errorMessage: String): SignUpStateUI.ValidationError {
        return when {
            errorMessage.contains("nombre", ignoreCase = true) -> {
                SignUpStateUI.ValidationError(firstNameError = errorMessage)
            }
            errorMessage.contains("apellido", ignoreCase = true) -> {
                SignUpStateUI.ValidationError(lastNameError = errorMessage)
            }
            errorMessage.contains("username", ignoreCase = true) -> {
                SignUpStateUI.ValidationError(usernameError = errorMessage)
            }
            errorMessage.contains("email", ignoreCase = true) -> {
                SignUpStateUI.ValidationError(emailError = errorMessage)
            }
            errorMessage.contains("contraseña", ignoreCase = true) -> {
                SignUpStateUI.ValidationError(passwordError = errorMessage)
            }
            else -> {
                SignUpStateUI.ValidationError()
            }
        }
    }

    fun clearState() {
        _state.value = SignUpStateUI.Init
    }
}