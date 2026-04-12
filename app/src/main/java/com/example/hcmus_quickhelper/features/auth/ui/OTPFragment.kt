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
import com.example.hcmus_quickhelper.databinding.FragmentOtpBinding
import com.example.hcmus_quickhelper.features.auth.datasource.AuthRemoteDataSource
import com.example.hcmus_quickhelper.features.auth.repository.AuthRepository
import com.example.hcmus_quickhelper.features.auth.viewmodel.AuthViewModel

class OTPFragment : Fragment() {
    private var _binding: FragmentOtpBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AuthViewModel
    private var userEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userEmail = arguments?.getString("email")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupUI()
        observeViewModel()

        // Reset states so old results don't trigger UI effects
        viewModel.resetVerifyResult()
        viewModel.resetSendOtpResult()

        // REMOVE any automatic viewModel.sendOtp(it) calls here.
        // The email was already sent by RegisterFragment's signUpWith call.

        binding.tvResendOtp.setOnClickListener {
            userEmail?.let {
                viewModel.sendOtp(it)
                Toast.makeText(context, "Resending code...", Toast.LENGTH_SHORT).show()
            }
        }
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
            val otp = binding.etOtp.text.toString().trim()
            if (otp.length == 6 && userEmail != null) {
                viewModel.verifyOtp(userEmail!!, otp)
            } else {
                Toast.makeText(context, "Please enter a valid 6-digit code", Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnVerify.isEnabled = !isLoading
        }

        viewModel.sendOtpResult.observe(viewLifecycleOwner) { result ->
            result?.onSuccess {
                Toast.makeText(context, "OTP sent to your email", Toast.LENGTH_SHORT).show()
            }?.onFailure { error ->
                Log.e("AUTH_ERROR", "Failed to send OTP", error)
                Toast.makeText(context, "Failed to send OTP: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.verifyResult.observe(viewLifecycleOwner) { result ->
            result?.onSuccess {
                Toast.makeText(context, "Verified! Please login.", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack(R.id.login_fragment, false)
            }?.onFailure { error ->
                Log.e("AUTH_ERROR", "Verification failed", error)
                Toast.makeText(context, "Verification failed: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
