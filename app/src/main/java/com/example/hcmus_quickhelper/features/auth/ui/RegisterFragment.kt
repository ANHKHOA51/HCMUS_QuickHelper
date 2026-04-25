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
import com.example.hcmus_quickhelper.databinding.FragmentRegisterBinding
import com.example.hcmus_quickhelper.features.auth.datasource.AuthRemoteDataSource
import com.example.hcmus_quickhelper.features.auth.repository.AuthRepository
import com.example.hcmus_quickhelper.features.auth.viewmodel.AuthViewModel

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupUI()
        observeViewModel()
    }

    private fun setupViewModel() {
        // Manual DI consistent with LoginFragment
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
        binding.btnRegister.setOnClickListener {
            // Step 1: Extract username
            val username = binding.etUsername.text.toString().trim()
            val fullname = binding.etFullname.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            val selectedRole = if (binding.rbCustomer.isChecked) "CUSTOMER" else "HELPER"
            
            // Check required fields (username is optional)
            if (fullname.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty() && password.isNotEmpty()) {
                val bundle = Bundle().apply {
                    putString("username", username) // Step 2: Add to bundle
                    putString("fullname", fullname)
                    putString("email", email)
                    putString("phone", phone)
                    putString("password", password)
                    putString("role", selectedRole)
                }
                // Navigate to OTP Fragment
                findNavController().navigate(R.id.action_register_to_otp, bundle)
            } else {
                Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Step 3: Social/Biometric logic removed (no click listeners for btnGoogle or btnTouchId)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvTabLogin.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnRegister.isEnabled = !isLoading
        }

        viewModel.registerResult.observe(viewLifecycleOwner) { result ->
            result?.onSuccess {
                Toast.makeText(context, "Account created successfully!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack() // Go back to login
            }?.onFailure { error ->
                Log.e("AUTH_ERROR", "Registration failed", error)
                val message = error.localizedMessage ?: "Could not create account"
                Toast.makeText(context, "Error: $message", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
