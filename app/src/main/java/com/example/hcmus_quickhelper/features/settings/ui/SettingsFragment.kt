package com.example.hcmus_quickhelper.features.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.hcmus_quickhelper.core.auth.UserPreferences
import com.example.hcmus_quickhelper.core.utils.LanguageUtils
import com.example.hcmus_quickhelper.databinding.FragmentSettingsBinding
import com.example.hcmus_quickhelper.features.settings.repository.SettingsRepository
import com.example.hcmus_quickhelper.features.settings.viewmodel.SettingsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale

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
        val userPreferences = UserPreferences(requireContext().applicationContext)
        val repository = SettingsRepository(userPreferences)
        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SettingsViewModel(repository) as T
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

        binding.switchDeviceNotifications.setOnCheckedChangeListener { _, isChecked ->
            viewModel.togglePushNotifications(isChecked)
        }

        binding.switchEmailNotifications.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleEmailNotifications(isChecked)
        }

        binding.btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.language.collectLatest { languageCode ->
                binding.tvCurrentLanguage.text = if (languageCode == LanguageUtils.LANG_VI) {
                    getString(R.string.vietnamese)
                } else {
                    getString(R.string.english)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.pushNotificationsEnabled.collectLatest { enabled ->
                if (binding.switchDeviceNotifications.isChecked != enabled) {
                    binding.switchDeviceNotifications.isChecked = enabled
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.emailNotificationsEnabled.collectLatest { enabled ->
                if (binding.switchEmailNotifications.isChecked != enabled) {
                    binding.switchEmailNotifications.isChecked = enabled
                }
            }
        }
    }

    private fun showLanguageDialog() {
        viewLifecycleOwner.lifecycleScope.launch {
            val languages = arrayOf(getString(R.string.english), getString(R.string.vietnamese))
            val languageCodes = arrayOf(LanguageUtils.LANG_EN, LanguageUtils.LANG_VI)
            
            // Get current language from ViewModel to set the initial checked item
            val currentLang = viewModel.language.first()
            val checkedItem = if (currentLang == LanguageUtils.LANG_VI) 1 else 0
            
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.select_language))
                .setSingleChoiceItems(languages, checkedItem) { dialog, which ->
                    val selectedCode = languageCodes[which]
                    if (selectedCode != currentLang) {
                        lifecycleScope.launch {
                            // Ensure language is saved before activity is recreated
                            val userPreferences = UserPreferences(requireContext().applicationContext)
                            userPreferences.updateLanguage(selectedCode)
                            
                            updateLocale(selectedCode)
                            dialog.dismiss()
                        }
                    } else {
                        dialog.dismiss()
                    }
                }
                .setNegativeButton(getString(R.string.cancel), null)
                .show()
        }
    }

    private fun updateLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        
        val config = resources.configuration
        config.setLocale(locale)
        
        // Update the context for current resources
        requireContext().createConfigurationContext(config)
        
        // Restart the activity to apply changes globally
        activity?.recreate()
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
