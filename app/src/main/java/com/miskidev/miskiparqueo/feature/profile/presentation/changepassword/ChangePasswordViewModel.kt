package com.miskidev.miskiparqueo.feature.profile.presentation.changepassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miskidev.miskiparqueo.feature.profile.domain.usecases.ChangePasswordUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChangePasswordViewModel(
    private val changePasswordUseCase: ChangePasswordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChangePasswordUIState>(ChangePasswordUIState.Idle)
    val uiState = _uiState.asStateFlow()

    fun changePassword(userId: String, currentPass: String, newPass: String, confirmPass: String) {
        if (newPass != confirmPass) {
            _uiState.value = ChangePasswordUIState.Error("Las contraseñas nuevas no coinciden.")
            return
        }

        if (newPass.length < 6) { // Asumiendo una regla de negocio
            _uiState.value = ChangePasswordUIState.Error("La nueva contraseña debe tener al menos 6 caracteres.")
            return
        }

        _uiState.value = ChangePasswordUIState.Loading
        viewModelScope.launch {
            changePasswordUseCase(userId, currentPass, newPass)
                .onSuccess {
                    _uiState.value = ChangePasswordUIState.Success("Contraseña actualizada con éxito.")
                }
                .onFailure {
                    _uiState.value = ChangePasswordUIState.Error(it.message ?: "Error al cambiar la contraseña.")
                }
        }
    }

    sealed class ChangePasswordUIState {
        object Idle : ChangePasswordUIState()
        object Loading : ChangePasswordUIState()
        data class Success(val message: String) : ChangePasswordUIState()
        data class Error(val message: String) : ChangePasswordUIState()
    }
}