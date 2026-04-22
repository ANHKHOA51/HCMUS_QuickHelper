package com.example.hcmus_quickhelper.features.profile.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.core.auth.SessionManager
import com.example.hcmus_quickhelper.databinding.FragmentProfileBinding
import com.example.hcmus_quickhelper.features.profile.datasource.ProfileRemoteDataSource
import com.example.hcmus_quickhelper.features.profile.repository.ProfileRepository
import com.example.hcmus_quickhelper.features.profile.viewmodel.ProfileViewModel
import com.example.hcmus_quickhelper.features.profile.viewmodel.ProfileUiState
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProfileViewModel
    private lateinit var credentialManager: CredentialManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
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
        val dataSource = ProfileRemoteDataSource()
        val repository = ProfileRepository(dataSource)
        
        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(repository) as T
            }
        }
        
        viewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]
    }

    private fun setupUI() {
        // Initial setup from current session
        val user = SessionManager.currentUser.value
        user?.let {
            binding.etFullname.setText(it.fullname)
            binding.etUsername.setText(it.username ?: "")
            binding.tvEmail.text = it.email
        }

        binding.btnSave.setOnClickListener {
            val fullname = binding.etFullname.text.toString().trim()
            val username = binding.etUsername.text.toString().trim()

            if (fullname.isNotEmpty()) {
                viewModel.saveProfile(username, fullname)
            } else {
                Toast.makeText(context, "Full name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnLogout.setOnClickListener {
            logout()
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun logout() {
        lifecycleScope.launch {
            // 1. Clear Supabase Session
            SessionManager.logout()

            // 2. Clear Credential Manager state (Google Sign-In selection)
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (e: Exception) {
                // Ignore errors on clearing state
            }

            // 3. Navigate back to login
            findNavController().navigate(R.id.login_fragment) {
                popUpTo(R.id.home_fragment) { inclusive = true }
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is ProfileUiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.btnSave.isEnabled = false
                    }
                    is ProfileUiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnSave.isEnabled = true
                        Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                        viewModel.resetToIdle()
                    }
                    is ProfileUiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnSave.isEnabled = true
                        Toast.makeText(context, "Update failed: ${state.message}", Toast.LENGTH_LONG).show()
                        viewModel.resetToIdle()
                    }
                    is ProfileUiState.Idle -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnSave.isEnabled = true
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
