package com.example.kotlinsupabase.utils

import android.util.Patterns

object InputValidator {
    private val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$".toRegex()

    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isEmpty() -> ValidationResult.Error("Email tidak boleh kosong")
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                ValidationResult.Error("Email tidak valid")
            else -> ValidationResult.Success
        }
    }

    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isEmpty() -> ValidationResult.Error("Password tidak boleh kosong")
            !password.matches(passwordPattern) ->
                ValidationResult.Error("Password harus lebih dari 8 karakter, mengandung huruf besar, kecil, & angka.")
            else -> ValidationResult.Success
        }
    }
}

sealed class ValidationResult {
    data object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}