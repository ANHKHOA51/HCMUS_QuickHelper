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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.core.utils.EmailUtils
import com.example.hcmus_quickhelper.databinding.FragmentOtpBinding
import com.example.hcmus_quickhelper.features.auth.datasource.AuthRemoteDataSource
import com.example.hcmus_quickhelper.features.auth.repository.AuthRepository
import com.example.hcmus_quickhelper.features.auth.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

class OTPFragment : Fragment() {
    private var _binding: FragmentOtpBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AuthViewModel
    private var generatedOtp: String = ""
    private val args: OTPFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupUI()
        observeViewModel()

        generatedOtp = (100000..999999).random().toString()
        sendOtpEmail(generatedOtp)
    }

    private fun setupViewModel() {
        val dataSource = AuthRemoteDataSource()
        val repository = AuthRepository(dataSource)
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T = AuthViewModel(repository) as T
        }
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
    }

    private fun setupUI() {
        binding.btnVerify.setOnClickListener {
            val enteredOtp = binding.etOtp.text.toString().trim()
            if (enteredOtp == generatedOtp) {
                handleOtpSuccess()
            } else {
                Toast.makeText(context, getString(R.string.invalid_otp), Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvResendOtp.setOnClickListener {
            generatedOtp = (100000..999999).random().toString()
            sendOtpEmail(generatedOtp)
            Toast.makeText(context, getString(R.string.resending_code), Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleOtpSuccess() {
        if (args.flow == "password_reset") {
            val action = OTPFragmentDirections.actionOtpToChangePassword(
                isFromOtp = true,
                email = args.email
            )
            findNavController().navigate(action)
        } else {
            val email = args.email
            val pass = arguments?.getString("password") ?: ""
            val name = arguments?.getString("fullname") ?: ""
            val phone = arguments?.getString("phone") ?: ""
            val username = arguments?.getString("username") ?: email.substringBefore("@")
            val role = arguments?.getString("role") ?: "CUSTOMER"
            
            // This call must trigger the repository to write to public.users
            viewModel.register(email, pass, name, phone, username, role)
        }
    }

    private fun sendOtpEmail(otp: String) {
        val recipientEmail = args.email
        val isPasswordReset = args.flow == "password_reset"
        
        val subject = if (isPasswordReset) {
            getString(R.string.otp_email_subject_reset)
        } else {
            getString(R.string.otp_email_subject_verify)
        }
        val body = if (isPasswordReset) {
            getString(R.string.otp_email_body_reset, otp)
        } else {
            getString(R.string.otp_email_body_verify, otp)
        }

        lifecycleScope.launch {
            val result = EmailUtils.sendEmail(recipientEmail, subject, body)
            
            if (isAdded) {
                result.onSuccess {
                    Toast.makeText(context, getString(R.string.otp_sent_success, recipientEmail), Toast.LENGTH_SHORT).show()
                }.onFailure {
                    Toast.makeText(context, getString(R.string.otp_send_failed), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnVerify.isEnabled = !isLoading
        }

        viewModel.registerResult.observe(viewLifecycleOwner) { result ->
            result?.onSuccess {
                Toast.makeText(context, getString(R.string.registration_successful), Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_otp_to_login)
            }?.onFailure { error ->
                Log.e("AUTH_ERROR", "Registration failed", error)
                val errorMessage = if (error.message?.contains("user_already_exists") == true) {
                    getString(R.string.error_user_exists)
                } else {
                    getString(R.string.error_prefix, error.message)
                }

                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()

                if (error.message?.contains("user_already_exists") == true) {
                    findNavController().navigate(R.id.action_otp_to_login)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
