package com.example.miskiparqueo.feature.auth.login.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miskiparqueo.feature.auth.domain.model.UserModel
import com.example.miskiparqueo.feature.auth.login.domain.usecases.LoginUseCase
import com.example.miskiparqueo.feature.auth.signup.domain.model.vo.Password
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    sealed class LoginStateUI {
        object Init : LoginStateUI()
        object Loading : LoginStateUI()
        data class Error(val message: String) : LoginStateUI()
        data class Success(val user: UserModel) : LoginStateUI()
        data class ValidationError(val message: String) : LoginStateUI()
    }

    private val _state = MutableStateFlow<LoginStateUI>(LoginStateUI.Init)
    val state: StateFlow<LoginStateUI> = _state.asStateFlow()

    fun login(credentialRaw: String, passwordRaw: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = LoginStateUI.Loading

            try {
                val password = Password.create(passwordRaw)

                val result = loginUseCase(credentialRaw, password)

                result.fold(
                    onSuccess = { user ->
                        _state.value = LoginStateUI.Success(user)
                    },
                    onFailure = { error ->
                        _state.value = LoginStateUI.Error(message = error.message ?: "Error desconocido")
                    }
                )

            } catch (e: IllegalArgumentException) {
                _state.value = LoginStateUI.ValidationError(e.message ?: "Datos inválidos")
            }
        }
    }

    fun clearState() {
        _state.value = LoginStateUI.Init
    }
}