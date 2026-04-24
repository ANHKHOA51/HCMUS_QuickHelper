package com.example.hcmus_quickhelper.features.community.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.example.hcmus_quickhelper.core.auth.SessionManager
import com.example.hcmus_quickhelper.core.model.User
import com.example.hcmus_quickhelper.databinding.FragmentFeedDetailBinding
import com.example.hcmus_quickhelper.databinding.FragmentPostFeedBinding
import com.example.hcmus_quickhelper.features.community.datasource.CommunityRemoteDataSource
import com.example.hcmus_quickhelper.features.community.repository.CommunityRepository
import com.example.hcmus_quickhelper.features.community.viewmodel.FeedDetailViewModel
import com.example.hcmus_quickhelper.features.community.viewmodel.PostFeedViewModel

class PostFeedFragment : Fragment() {
    private var _binding: FragmentPostFeedBinding? = null
    private val binding get() = _binding!!

    val currentUser: User? = SessionManager.currentUser.asLiveData().value

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUserId = currentUser?.id ?: -1
        setupViewModel()
        setupUI(currentUserId)
    }

    private lateinit var viewModel: PostFeedViewModel

    private fun setupViewModel() {
        val dataSource = CommunityRemoteDataSource()
        val repository = CommunityRepository(dataSource)

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(PostFeedViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return PostFeedViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

        viewModel = ViewModelProvider(this, factory)[PostFeedViewModel::class.java]
    }

    private fun setupUI(currentUserId: Int) {
        if (currentUserId == -1) return

        binding.btnPost.setOnClickListener {
            binding.btnPost.setEnabled(false)
            val content = binding.etPostContent.text.toString().trim()

            if (content.isEmpty()) return@setOnClickListener

            viewModel.postFeed(currentUserId, content) {
                binding.btnPost.setEnabled(true)
                findNavController().popBackStack()
            }

        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}