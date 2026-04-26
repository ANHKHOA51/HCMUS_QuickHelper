package com.example.hcmus_quickhelper.features.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.core.auth.SessionManager
import com.example.hcmus_quickhelper.databinding.FragmentChangePasswordBinding
import com.example.hcmus_quickhelper.features.auth.datasource.AuthRemoteDataSource
import com.example.hcmus_quickhelper.features.auth.repository.AuthRepository
import com.example.hcmus_quickhelper.features.settings.viewmodel.ChangePasswordUiState
import com.example.hcmus_quickhelper.features.settings.viewmodel.ChangePasswordViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ChangePasswordFragment : Fragment() {

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ChangePasswordViewModel
    private val args: ChangePasswordFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupUI()
        observeViewModel()

        if (args.isFromOtp) {
            showResetState()
        }
    }

    private fun setupViewModel() {
        val dataSource = AuthRemoteDataSource()
        val repository = AuthRepository(dataSource)
        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ChangePasswordViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[ChangePasswordViewModel::class.java]
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnVerify.setOnClickListener {
            val oldPass = binding.etOldPassword.text.toString()
            if (oldPass.isNotEmpty()) {
                viewModel.verifyOldPassword(oldPass)
            } else {
                Toast.makeText(context, getString(R.string.error_enter_current_password), Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvForgotPassword.setOnClickListener {
            lifecycleScope.launch {
                val email = SessionManager.currentUser.value?.email 
                    ?: SessionManager.currentUser.firstOrNull()?.email
                if (email != null) {
                    val action = ChangePasswordFragmentDirections.actionChangePasswordToOtp(
                        email = email,
                        flow = "password_reset"
                    )
                    findNavController().navigate(action)
                } else {
                    Toast.makeText(context, getString(R.string.error_identify_session), Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnSavePassword.setOnClickListener {
            val newPass = binding.etNewPassword.text.toString()
            val confirmPass = binding.etConfirmPassword.text.toString()

            if (newPass.length < 6) {
                Toast.makeText(context, getString(R.string.error_password_min_length), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPass == confirmPass) {
                if (args.isFromOtp && args.email != null) {
                    viewModel.updatePasswordByEmail(args.email!!, newPass)
                } else {
                    viewModel.updatePassword(newPass)
                }
            } else {
                Toast.makeText(context, getString(R.string.error_passwords_dont_match), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ChangePasswordUiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    setInputsEnabled(false)
                }
                is ChangePasswordUiState.Verified -> {
                    binding.progressBar.visibility = View.GONE
                    setInputsEnabled(true)
                    showResetState()
                }
                is ChangePasswordUiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(context, getString(R.string.password_updated_success), Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                is ChangePasswordUiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    setInputsEnabled(true)
                    Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                    viewModel.resetToIdle()
                }
                is ChangePasswordUiState.Idle -> {
                    binding.progressBar.visibility = View.GONE
                    setInputsEnabled(true)
                }
            }
        }
    }

    private fun showResetState() {
        binding.layoutVerify.visibility = View.GONE
        binding.layoutReset.visibility = View.VISIBLE
    }

    private fun setInputsEnabled(enabled: Boolean) {
        binding.btnVerify.isEnabled = enabled
        binding.btnSavePassword.isEnabled = enabled
        binding.etOldPassword.isEnabled = enabled
        binding.etNewPassword.isEnabled = enabled
        binding.etConfirmPassword.isEnabled = enabled
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
