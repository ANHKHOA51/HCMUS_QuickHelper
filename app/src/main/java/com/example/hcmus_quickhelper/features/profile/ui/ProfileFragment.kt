package com.example.hcmus_quickhelper.features.profile.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.hcmus_quickhelper.core.auth.SessionManager
import com.example.hcmus_quickhelper.databinding.FragmentProfileBinding
import com.example.hcmus_quickhelper.features.profile.datasource.ProfileRemoteDataSource
import com.example.hcmus_quickhelper.features.profile.repository.ProfileRepository
import com.example.hcmus_quickhelper.features.profile.viewmodel.ProfileViewModel

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProfileViewModel

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
        
        setupViewModel()
        setupUI()
        observeViewModel()
    }

    private fun setupViewModel() {
        val dataSource = ProfileRemoteDataSource()
        val repository = ProfileRepository(dataSource)
        
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(repository) as T
            }
        }
        
        viewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]
    }

    private fun setupUI() {
        val user = SessionManager.currentUser.value
        user?.let {
            binding.etFullname.setText(it.fullname)
            binding.etUsername.setText(it.username ?: "")
            binding.tvEmail.text = it.email
            binding.tvPhone.text = it.phone
            binding.tvRole.text = it.role
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

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnSave.isEnabled = !isLoading
        }

        viewModel.updateStatus.observe(viewLifecycleOwner) { result ->
            result?.onSuccess {
                Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
            }?.onFailure { error ->
                Toast.makeText(context, "Update failed: ${error.message}", Toast.LENGTH_LONG).show()
            }
            viewModel.resetUpdateStatus()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
