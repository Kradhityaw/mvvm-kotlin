package com.example.kotlinsupabase.data.repository

import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.providers.builtin.IDToken
import com.example.kotlinsupabase.data.remote.SupabaseClient

class AuthRepository {
    private val supabase = SupabaseClient.client

    suspend fun signInWithEmail(email: String, password: String) {
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun signInWithGoogle(idToken: String) {
        supabase.auth.signInWith(IDToken) {
            this.idToken = idToken
            provider = Google
        }
    }

    suspend fun signOut() {
        supabase.auth.signOut()
    }

    fun getCurrentSession() = supabase.auth.currentSessionOrNull()
}