package com.example.kotlinsupabase.domain.model

sealed class AuthEvent {
    data object NavigateToHome : AuthEvent()
    data class ShowError(val message: String) : AuthEvent()
}