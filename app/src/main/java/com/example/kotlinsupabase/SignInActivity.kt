package com.example.kotlinsupabase

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.kotlinsupabase.databinding.ActivitySignInBinding
import com.example.kotlinsupabase.domain.model.AuthResult
import com.example.kotlinsupabase.presentation.viewmodel.SignInViewModel
import com.example.kotlinsupabase.utils.GoogleAuthHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.launch

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private val viewModel: SignInViewModel by viewModels()
    private lateinit var googleAuthHelper: GoogleAuthHelper

    private val googleAuthLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleGoogleSignInResult(task)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        googleAuthHelper = GoogleAuthHelper(this)
        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.authState.collect { state ->
                when (state) {
                    is AuthResult.Loading -> showLoading()
                    is AuthResult.Success -> {
                        hideLoading()
                        handleSuccessLogin()
                    }
                    is AuthResult.Error -> {
                        hideLoading()
                        showError(state.message)
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.emailError.collect { error ->
                binding.etEmailBorder.error = error
            }
        }

        lifecycleScope.launch {
            viewModel.passwordError.collect { error ->
                binding.etPasswordBorder.error = error
            }
        }
    }

    private fun setupListeners() {
        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            viewModel.signInWithEmail(email, password)
        }

        binding.btnGoogleSignIn.setOnClickListener {
            val signInIntent = googleAuthHelper.googleSignInClient.signInIntent
            googleAuthLauncher.launch(signInIntent)
        }
    }

    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val idToken = account.idToken

            if (idToken != null) {
                viewModel.signInWithGoogle(idToken)
            } else {
                showError("Gagal mendapatkan token dari Google")
            }
        } catch (e: ApiException) {
            Log.e("Auth", "Google sign in failed: ${e.statusCode}")
            showError("Login Google gagal")
        }
    }

    private fun handleSuccessLogin() {
        Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show()
        // Navigate to MainActivity
        // val intent = Intent(this, MainActivity::class.java)
        // startActivity(intent)
        // finish()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSignIn.isEnabled = false
        binding.btnGoogleSignIn.isEnabled = false
        binding.etEmail.isEnabled = false
        binding.etPassword.isEnabled = false
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.btnSignIn.isEnabled = true
        binding.btnGoogleSignIn.isEnabled = true
        binding.etEmail.isEnabled = true
        binding.etPassword.isEnabled = true
    }
}