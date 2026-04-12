package com.example.hcmus_quickhelper.features.community.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import coil.load
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.core.utils.toRelativeTime
import com.example.hcmus_quickhelper.databinding.FragmentFeedDetailBinding
import com.example.hcmus_quickhelper.features.community.datasource.CommunityRemoteDataSource
import com.example.hcmus_quickhelper.features.community.repository.CommunityRepository
import com.example.hcmus_quickhelper.features.community.viewmodel.FeedDetailViewModel
import kotlin.text.lowercase

class FeedDetailFragment : Fragment() {
    private var _binding: FragmentFeedDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        observeViewModel()

        val feedId = arguments?.getInt("feedId")!!

        viewModel.fetchFeedDetail(feedId, 3)
    }

    private lateinit var viewModel: FeedDetailViewModel
    private lateinit var commentAdapter: CommentAdapter

    private fun setupViewModel() {
        val dataSource = CommunityRemoteDataSource()
        val repository = CommunityRepository(dataSource)

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(FeedDetailViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return FeedDetailViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

        viewModel = ViewModelProvider(this, factory)[FeedDetailViewModel::class.java]
    }

    private fun setupRecyclerView() {
        commentAdapter = CommentAdapter(emptyList())

        binding.btnBack.setOnClickListener {
            view?.findNavController()?.navigateUp()
        }

        binding.rvComments.apply {
            adapter = commentAdapter
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }


    private fun observeViewModel() {
        viewModel.feedContent.observe(viewLifecycleOwner) { item ->
            item?.let {
                binding.tvName.text = it.ownerFullname
                binding.tvHandle.text = "@${it.ownerUsername ?: ""}"
                binding.tvContent.text = it.feedContent
                binding.tvTag.text = "#${it.ownerRole.lowercase()}"
                binding.tvTime.text = it.createdAt.toRelativeTime()

                binding.ivAvatar.load(it.ownerAvatar) {
                    placeholder(R.drawable.default_avt)
                    error(R.drawable.default_avt)
                    transformations(coil.transform.CircleCropTransformation())
                }

                if (it.isLiked) {
                    binding.ivHeart.setImageResource(R.drawable.ic_heart_filled)
                } else {
                    binding.ivHeart.setImageResource(R.drawable.ic_heart)
                }

                binding.tvHeart.text = it.likeCount.toString()
                binding.tvCmt.text = it.commentCount.toString()
            }
        }

        viewModel.commentList.observe(viewLifecycleOwner) { list ->
            commentAdapter.updateData(list)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}