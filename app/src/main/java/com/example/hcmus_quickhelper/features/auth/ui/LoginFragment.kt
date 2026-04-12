package com.example.hcmus_quickhelper.features.auth.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.databinding.FragmentLoginBinding
import com.example.hcmus_quickhelper.features.auth.datasource.AuthRemoteDataSource
import com.example.hcmus_quickhelper.features.auth.repository.AuthRepository
import com.example.hcmus_quickhelper.features.auth.viewmodel.AuthViewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AuthViewModel

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

        setupViewModel()
        setupUI()
        observeViewModel()
    }

    private fun setupViewModel() {
        // Manual DI as requested in the plan
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

        binding.tvTabRegister.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnLogin.isEnabled = !isLoading
        }

        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            result?.onSuccess { user ->
                Toast.makeText(context, "Welcome back, ${user.fullname}", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.chat_list_fragment)
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
