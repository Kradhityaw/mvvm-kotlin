package com.example.kotlinsupabase.utils

import io.github.jan.supabase.exceptions.RestException
import java.net.UnknownHostException
import java.net.SocketTimeoutException

object ErrorHandler {
    fun getErrorMessage(exception: Exception): String {
        return when (exception) {
            is RestException -> handleRestException(exception)
            is UnknownHostException -> "Tidak ada koneksi internet. Periksa koneksi Anda"
            is SocketTimeoutException -> "Koneksi timeout. Silakan coba lagi"
            else -> "Terjadi kesalahan: ${exception.message}"
        }
    }

    private fun handleRestException(exception: RestException): String {
        return when {
            exception.message?.contains("Invalid login credentials") == true ->
                "Email atau password salah"
            exception.message?.contains("Email not confirmed") == true ->
                "Email belum diverifikasi. Silakan cek inbox Anda"
            exception.message?.contains("User not found") == true ->
                "Akun tidak ditemukan. Silakan daftar terlebih dahulu"
            exception.message?.contains("invalid_grant") == true ->
                "Token Google tidak valid. Silakan coba lagi"
            else -> "Login gagal: ${exception.message}"
        }
    }
}