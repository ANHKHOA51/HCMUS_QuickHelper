package com.example.hcmus_quickhelper.features.community.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.navigation.findNavController
import coil.load
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.core.auth.SessionManager
import com.example.hcmus_quickhelper.core.model.User
import com.example.hcmus_quickhelper.core.utils.toRelativeTime
import com.example.hcmus_quickhelper.databinding.FragmentFeedDetailBinding
import com.example.hcmus_quickhelper.features.community.datasource.CommunityRemoteDataSource
import com.example.hcmus_quickhelper.features.community.repository.CommunityRepository
import com.example.hcmus_quickhelper.features.community.viewmodel.FeedDetailViewModel
import kotlin.text.lowercase

class FeedDetailFragment : Fragment() {
    private var _binding: FragmentFeedDetailBinding? = null
    private val binding get() = _binding!!

    var feedId: Int? = null
    val currentUser: User? = SessionManager.currentUser.asLiveData().value

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

        val currentUserId = currentUser?.id ?: -1

        setupViewModel()
        setupRecyclerView(currentUserId)
        observeViewModel()

        feedId = arguments?.getInt("feedId")!!

        viewModel.fetchFeedDetail(feedId, currentUserId)
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
                    return FeedDetailViewModel(repository, currentUser) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

        viewModel = ViewModelProvider(this, factory)[FeedDetailViewModel::class.java]
    }

    private fun setupRecyclerView(currentUserId: Int) {
        commentAdapter = CommentAdapter(emptyList())

        binding.btnBack.setOnClickListener {
            view?.findNavController()?.navigateUp()
        }

        binding.rvComments.apply {
            adapter = commentAdapter
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        binding.btnSend.setOnClickListener {
            if (feedId == null) return@setOnClickListener

            val content = binding.etComment.text.toString().trim()

            if (content.isEmpty()) return@setOnClickListener

            viewModel.postComment(
                feedId!!,
                currentUserId,
                content
            )

            binding.etComment.text?.clear()
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

                binding.cbHeart.setOnCheckedChangeListener(null)
                binding.cbHeart.isChecked = item.isLiked
                binding.tvHeart.text = item.likeCount.toString()
                binding.tvCmt.text = item.commentCount.toString()

                binding.cbHeart.setOnClickListener {
                    viewModel.toggleLike(viewModel.feedContent.value, currentUser?.id ?: -1)
                }
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