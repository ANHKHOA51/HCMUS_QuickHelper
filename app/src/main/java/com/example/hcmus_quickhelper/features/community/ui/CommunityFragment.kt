package com.example.hcmus_quickhelper.features.community.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.hcmus_quickhelper.core.auth.SessionManager
import com.example.hcmus_quickhelper.databinding.FragmentFeedBinding
import com.example.hcmus_quickhelper.features.community.datasource.CommunityRemoteDataSource
import com.example.hcmus_quickhelper.features.community.repository.CommunityRepository
import com.example.hcmus_quickhelper.features.community.viewmodel.CommunityViewModel

class CommunityFragment : Fragment() {
    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private var chipType = "All"
    val currentUserId: Int = SessionManager.currentUser.asLiveData().value?.id ?: -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        observeViewModel()

        viewModel.fetchFeeds(currentUserId)
    }

    private lateinit var viewModel: CommunityViewModel
    private lateinit var feedAdapter: FeedAdapter

    private fun setupViewModel() {
        val dataSource = CommunityRemoteDataSource()
        val repository = CommunityRepository(dataSource)

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(CommunityViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return CommunityViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

        viewModel = ViewModelProvider(this, factory)[CommunityViewModel::class.java]
    }

    private fun setupRecyclerView() {
        feedAdapter = FeedAdapter(emptyList(), currentUserId)

        binding.rvFeed.apply {
            adapter = feedAdapter
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        binding.chipGroupFilter.setOnCheckedStateChangeListener { group, checkedIds ->
            // checkedIds là một danh sách các ID được chọn (vì singleSelection nên lấy cái đầu tiên)
            val checkedId = checkedIds.firstOrNull()

            when (checkedId) {
                binding.chipAll.id -> {
                    if (chipType != "All") {
                        chipType = "All"
                        viewModel.fetchFeeds(currentUserId)
                    }
                }
                binding.chipPopular.id -> {
                    if (chipType != "Popular") {
                        chipType = "Popular"

                        viewModel.fetchPopularFeeds(currentUserId)
                    }
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.feedList.observe(viewLifecycleOwner) { list ->
            feedAdapter.updateData(list)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}