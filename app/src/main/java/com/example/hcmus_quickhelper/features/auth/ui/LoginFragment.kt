package com.example.hcmus_quickhelper.features.auth.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.core.auth.SessionManager
import com.example.hcmus_quickhelper.core.database.SupabaseConfig
import com.example.hcmus_quickhelper.core.model.UserRole
import com.example.hcmus_quickhelper.databinding.FragmentLoginBinding
import com.example.hcmus_quickhelper.features.auth.datasource.AuthRemoteDataSource
import com.example.hcmus_quickhelper.features.auth.repository.AuthRepository
import com.example.hcmus_quickhelper.features.auth.viewmodel.AuthViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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
        
        // Auto-login check (Reactive)
        viewLifecycleOwner.lifecycleScope.launch {
            SessionManager.currentUser.collectLatest { user ->
                user?.let {
                    navigateToDashboard(it.role)
                }
            }
        }
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

    private fun getFcmToken(onComplete: (String?) -> Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                onComplete(null)
                return@addOnCompleteListener
            }
            onComplete(task.result)
        }
    }

    private fun setupUI() {
        binding.btnLogin.setOnClickListener {
            val identifier = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            
            if (identifier.isNotEmpty() && password.isNotEmpty()) {
                getFcmToken { token ->
                    viewModel.login(identifier, password, token)
                }
            } else {
                Toast.makeText(context, "Please enter email/phone and password", Toast.LENGTH_SHORT).show()
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
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(SupabaseConfig.GOOGLE_WEB_CLIENT_ID)
            .setAutoSelectEnabled(true)
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
                Log.e("GOOGLE_AUTH", "User cancelled or no Google account found")
            } catch (e: Exception) {
                Log.e("GOOGLE_AUTH", "Error: ${e.message}")
            }
        }
    }

    private fun handleSignIn(result: androidx.credentials.GetCredentialResponse) {
        val credential = result.credential
        if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken
                
                getFcmToken { token ->
                    viewModel.signInWithGoogle(idToken, token)
                }
            } catch (e: Exception) {
                Log.e("GOOGLE_AUTH", "Failed to parse Google ID Token", e)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnLogin.isEnabled = !isLoading
            binding.btnGoogle.isEnabled = !isLoading
        }

        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            result?.onSuccess { user ->
                lifecycleScope.launch {
                    SessionManager.login(user)
                    Toast.makeText(context, "Welcome back, ${user.fullname}", Toast.LENGTH_SHORT).show()
                    navigateToDashboard(user.role)
                }
            }?.onFailure { error ->
                Log.e("AUTH_ERROR", "Login failed", error)
                val message = when {
                    error.message?.contains("401") == true -> "Invalid email/phone or password"
                    else -> error.localizedMessage ?: "Connection error"
                }
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun navigateToDashboard(role: String) {
        if (role == UserRole.HELPER.toString()) {
            findNavController().navigate(R.id.action_login_fragment_to_dashboard_helper_fragment)
        } else {
            findNavController().navigate(R.id.home_fragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
