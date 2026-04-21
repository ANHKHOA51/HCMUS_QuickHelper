package com.example.hcmus_quickhelper.features.auth.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.core.database.SupabaseConfig
import com.example.hcmus_quickhelper.databinding.FragmentLoginBinding
import com.example.hcmus_quickhelper.features.auth.datasource.AuthRemoteDataSource
import com.example.hcmus_quickhelper.features.auth.repository.AuthRepository
import com.example.hcmus_quickhelper.features.auth.viewmodel.AuthViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch
import java.security.SecureRandom
import java.util.Base64

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AuthViewModel
    private lateinit var credentialManager: CredentialManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        credentialManager = CredentialManager.create(requireContext())
        setupViewModel()
        setupUI()
        observeViewModel()
    }

    private fun setupViewModel() {
        val dataSource = AuthRemoteDataSource()
        val repository = AuthRepository(dataSource)
        
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(repository) as T
            }
        }
        
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
    }

    private fun setupUI() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            
            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(email, password)
            } else {
                Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnGoogle.setOnClickListener {
            triggerGoogleSignIn()
        }

        binding.tvTabRegister.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }
    }

    private fun triggerGoogleSignIn() {
        val googleIdOption = GetGoogleIdOption.Builder()
            // Start with false to ensure the selector always pops up for new users
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(SupabaseConfig.GOOGLE_WEB_CLIENT_ID)
            .setAutoSelectEnabled(true) // Smooth experience for returning users
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = requireContext()
                )
                handleSignIn(result)
            } catch (e: GetCredentialException) {
                // This happens if the user cancels or no account is found
                Log.e("GOOGLE_AUTH", "GetCredentialException: ${e.message}")
                Toast.makeText(context, "Please sign into a Google account on this device", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("GOOGLE_AUTH", "Error: ${e.message}")
            }
        }
    }

    private suspend fun retryGoogleSignInWithoutFilter() {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(SupabaseConfig.GOOGLE_WEB_CLIENT_ID)
            //.setNonce(generateSecureRandomNonce())
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            val result = credentialManager.getCredential(
                request = request,
                context = requireContext()
            )
            handleSignIn(result)
        } catch (e: Exception) {
            Log.e("GOOGLE_AUTH", "Retry failed: ${e.message}", e)
            Toast.makeText(context, "Google Sign-In Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleSignIn(result: androidx.credentials.GetCredentialResponse) {
        val credential = result.credential
        if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken
                viewModel.signInWithGoogle(idToken)
            } catch (e: Exception) {
                Log.e("GOOGLE_AUTH", "Failed to parse Google ID Token", e)
            }
        } else {
            Log.e("GOOGLE_AUTH", "Unexpected credential type: ${credential.type}")
        }
    }

    private fun generateSecureRandomNonce(): String {
        val rawNonce = ByteArray(32)
        SecureRandom().nextBytes(rawNonce)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(rawNonce)
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnLogin.isEnabled = !isLoading
            binding.btnGoogle.isEnabled = !isLoading
        }

        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            result?.onSuccess { user ->
                Toast.makeText(context, "Welcome back, ${user.fullname}", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.home_fragment)
            }?.onFailure { error ->
                Log.e("AUTH_ERROR", "Login failed", error)
                val message = when {
                    error.message?.contains("401") == true || error.message?.contains("Invalid email or password") == true -> "Invalid email or password"
                    else -> error.localizedMessage ?: "Connection error"
                }
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
