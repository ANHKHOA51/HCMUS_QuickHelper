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
import com.example.hcmus_quickhelper.databinding.FragmentOtpBinding
import com.example.hcmus_quickhelper.features.auth.datasource.AuthRemoteDataSource
import com.example.hcmus_quickhelper.features.auth.repository.AuthRepository
import com.example.hcmus_quickhelper.features.auth.viewmodel.AuthViewModel
import com.example.hcmus_quickhelper.features.auth.ui.OTPFragmentArgs
import com.example.hcmus_quickhelper.features.auth.ui.OTPFragmentDirections
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Properties
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

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
                Toast.makeText(context, "Invalid OTP", Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvResendOtp.setOnClickListener {
            generatedOtp = (100000..999999).random().toString()
            sendOtpEmail(generatedOtp)
            Toast.makeText(context, "Resending code...", Toast.LENGTH_SHORT).show()
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
            val username = arguments?.getString("username")
            val role = arguments?.getString("role") ?: "CUSTOMER"
            viewModel.register(email, pass, name, phone, username, role)
        }
    }

    private fun sendOtpEmail(otp: String) {
        val recipientEmail = args.email
        
        val senderEmail = com.example.hcmus_quickhelper.BuildConfig.BUSINESS_EMAIL
        val senderPassword = com.example.hcmus_quickhelper.BuildConfig.BUSINESS_APP_PASS

        val props = Properties().apply {
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.socketFactory.port", "465")
            put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
            put("mail.smtp.auth", "true")
            put("mail.smtp.port", "465")
        }

        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(senderEmail, senderPassword)
            }
        })

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress(senderEmail))
                    addRecipient(Message.RecipientType.TO, InternetAddress(recipientEmail))
                    subject = if (args.flow == "password_reset") "Password Reset OTP" else "Your Verification Code"
                    val body = if (args.flow == "password_reset") {
                        "Your OTP for resetting your HCMUS QuickHelper password is: $otp"
                    } else {
                        "Your OTP code is: $otp. Please enter this in the HCMUS QuickHelper app."
                    }
                    setText(body)
                }
                Transport.send(message)
                
                withContext(Dispatchers.Main) {
                    if (isAdded) {
                        Toast.makeText(context, "OTP sent to $recipientEmail", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("SMTP_ERROR", "Failed to send email", e)
                withContext(Dispatchers.Main) {
                    if (isAdded) {
                        Toast.makeText(context, "Failed to send email automatically.", Toast.LENGTH_SHORT).show()
                    }
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
                Toast.makeText(context, "Registration Successful!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_otp_to_login)
            }?.onFailure { error ->
                Log.e("AUTH_ERROR", "Registration failed", error)
                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
