package com.example.hcmus_quickhelper.features.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.databinding.FragmentSettingsBinding
import com.example.hcmus_quickhelper.features.settings.repository.SettingsRepository
import com.example.hcmus_quickhelper.features.settings.viewmodel.SettingsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SettingsViewModel
    private lateinit var credentialManager: CredentialManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
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
        val repository = SettingsRepository()
        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SettingsViewModel(repository, requireContext().applicationContext) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[SettingsViewModel::class.java]
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.cvEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_settings_to_profile)
        }

        binding.cvChangePassword.setOnClickListener {
            findNavController().navigate(R.id.action_settings_to_change_password)
        }

        binding.cvLanguage.setOnClickListener {
            showLanguageDialog()
        }

        binding.btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.language.collectLatest { language ->
                binding.tvCurrentLanguage.text = language
            }
        }
    }

    private fun showLanguageDialog() {
        val languages = arrayOf("English", "Vietnamese")
        AlertDialog.Builder(requireContext())
            .setTitle("Select Language")
            .setItems(languages) { _, which ->
                val selected = languages[which]
                viewModel.updateLanguage(selected)
                Toast.makeText(context, "Language changed to $selected", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun logout() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.logout()
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (e: Exception) {
                // Ignore errors
            }
            
            val navOptions = navOptions {
                popUpTo(findNavController().graph.id) {
                    inclusive = true
                }
            }
            
            try {
                findNavController().navigate(R.id.login_fragment, null, navOptions)
            } catch (e: Exception) {
                findNavController().navigate(R.id.login_fragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}