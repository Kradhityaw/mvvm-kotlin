package com.example.kotlinsupabase.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinsupabase.data.repository.AuthRepository
import com.example.kotlinsupabase.domain.model.AuthResult
import com.example.kotlinsupabase.utils.ErrorHandler
import com.example.kotlinsupabase.utils.InputValidator
import com.example.kotlinsupabase.utils.ValidationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignInViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthResult>(AuthResult.Success)
    val authState: StateFlow<AuthResult> = _authState

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError

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
        _authState.value = AuthResult.Loading
        viewModelScope.launch {
            try {
                authRepository.signInWithEmail(email, password)
                _authState.value = AuthResult.Success
            } catch (e: Exception) {
                val errorMessage = ErrorHandler.getErrorMessage(e)
                _authState.value = AuthResult.Error(errorMessage)
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        _authState.value = AuthResult.Loading
        viewModelScope.launch {
            try {
                authRepository.signInWithGoogle(idToken)
                _authState.value = AuthResult.Success
            } catch (e: Exception) {
                val errorMessage = ErrorHandler.getErrorMessage(e)
                _authState.value = AuthResult.Error(errorMessage)
            }
        }
    }

    fun resetErrors() {
        _emailError.value = null
        _passwordError.value = null
    }
}