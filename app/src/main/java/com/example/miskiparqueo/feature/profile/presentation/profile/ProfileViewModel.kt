package com.example.miskiparqueo.feature.profile.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miskiparqueo.feature.auth.domain.model.UserModel
import com.example.miskiparqueo.feature.auth.domain.usecases.GetUserByIdUseCase
import com.example.miskiparqueo.feature.auth.signup.domain.model.vo.Email
import com.example.miskiparqueo.feature.auth.signup.domain.model.vo.FirstName
import com.example.miskiparqueo.feature.auth.signup.domain.model.vo.LastName
import com.example.miskiparqueo.feature.auth.signup.domain.model.vo.Username
import com.example.miskiparqueo.feature.profile.domain.usecases.UpdateUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val updateUserUseCase: UpdateUserUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase // Se inyecta el nuevo UseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUIState>(ProfileUIState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _userState = MutableStateFlow<UserModel?>(null)
    val userState = _userState.asStateFlow()

    // Nueva función para cargar el perfil usando el ID
    fun loadUserById(userId: String) {
        viewModelScope.launch {
            _uiState.value = ProfileUIState.Loading
            getUserByIdUseCase(userId)
                .onSuccess { user ->
                    _userState.value = user
                    _uiState.value = ProfileUIState.Idle // Vuelve a Idle una vez cargado
                }
                .onFailure {
                    _uiState.value = ProfileUIState.Error(it.message ?: "No se pudo cargar el perfil")
                }
        }
    }

    fun onFirstNameChanged(firstName: String) {
        _userState.value = _userState.value?.copy(firstName = FirstName.create(firstName))
    }

    fun onLastNameChanged(lastName: String) {
        _userState.value = _userState.value?.copy(lastName = LastName.create(lastName))
    }

    fun onUsernameChanged(username: String) {
        _userState.value = _userState.value?.copy(username = Username.create(username))
    }

    fun onEmailChanged(email: String) {
        _userState.value = _userState.value?.copy(email = Email.create(email))
    }

    fun saveChanges() {
        val currentUser = _userState.value ?: return
        _uiState.value = ProfileUIState.Loading

        viewModelScope.launch {
            updateUserUseCase(currentUser)
                .onSuccess {
                    _uiState.value = ProfileUIState.Success("Perfil actualizado con éxito")
                }
                .onFailure {
                    _uiState.value = ProfileUIState.Error(it.message ?: "Error desconocido")
                }
        }
    }

    sealed class ProfileUIState {
        object Idle : ProfileUIState()
        object Loading : ProfileUIState()
        data class Success(val message: String) : ProfileUIState()
        data class Error(val message: String) : ProfileUIState()
    }
}