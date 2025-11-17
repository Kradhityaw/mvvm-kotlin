package com.example.kotlinsupabase.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinsupabase.data.repository.AuthRepository
import com.example.kotlinsupabase.domain.model.AuthEvent
import com.example.kotlinsupabase.utils.ErrorHandler
import com.example.kotlinsupabase.utils.InputValidator
import com.example.kotlinsupabase.utils.ValidationResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SignInViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    // State untuk loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // State untuk error input
    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError

    // Channel untuk single events (tidak menyimpan state)
    private val _events = Channel<AuthEvent>()
    val events = _events.receiveAsFlow()

    fun signInWithEmail(email: String, password: String) {
        // Validasi input
        val emailValidation = InputValidator.validateEmail(email)
        val passwordValidation = InputValidator.validatePassword(password)

        _emailError.value = when (emailValidation) {
            is ValidationResult.Error -> emailValidation.message
            is ValidationResult.Success -> null
        }

        _passwordError.value = when (passwordValidation) {
            is ValidationResult.Error -> passwordValidation.message
            is ValidationResult.Success -> null
        }

        if (emailValidation is ValidationResult.Error ||
            passwordValidation is ValidationResult.Error) {
            return
        }

        // Proses sign in
        _isLoading.value = true
        viewModelScope.launch {
            try {
                authRepository.signInWithEmail(email, password)
                _isLoading.value = false
                // Kirim event navigate (hanya sekali)
                _events.send(AuthEvent.NavigateToHome)
            } catch (e: Exception) {
                _isLoading.value = false
                val errorMessage = ErrorHandler.getErrorMessage(e)
                // Kirim event error (hanya sekali)
                _events.send(AuthEvent.ShowError(errorMessage))
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                authRepository.signInWithGoogle(idToken)
                _isLoading.value = false
                _events.send(AuthEvent.NavigateToHome)
            } catch (e: Exception) {
                _isLoading.value = false
                val errorMessage = ErrorHandler.getErrorMessage(e)
                _events.send(AuthEvent.ShowError(errorMessage))
            }
        }
    }

    fun resetErrors() {
        _emailError.value = null
        _passwordError.value = null
    }
}