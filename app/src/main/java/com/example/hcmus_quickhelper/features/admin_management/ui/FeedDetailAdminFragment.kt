package com.example.hcmus_quickhelper.features.admin_management.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.core.auth.SessionManager
import com.example.hcmus_quickhelper.core.model.User
import com.example.hcmus_quickhelper.core.utils.toRelativeTime
import com.example.hcmus_quickhelper.databinding.FragmentFeedDetailBinding
import com.example.hcmus_quickhelper.features.admin_management.datasource.ManagementDataSource
import com.example.hcmus_quickhelper.features.admin_management.repository.ManagementRepository
import com.example.hcmus_quickhelper.features.admin_management.viewmodel.FeedDetailAdminViewModel
import com.example.hcmus_quickhelper.features.community.datasource.CommunityRemoteDataSource
import com.example.hcmus_quickhelper.features.community.repository.CommunityRepository
import com.example.hcmus_quickhelper.features.community.ui.CommentAdapter
import com.example.hcmus_quickhelper.features.community.viewmodel.FeedDetailViewModel
import kotlin.text.lowercase

class FeedDetailAdminFragment : Fragment() {
    private var _binding: FragmentFeedDetailBinding? = null
    private val binding get() = _binding!!

    var feedId: Int? = null

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

        feedId = arguments?.getInt("feedId")!!

        viewModel.fetchFeedDetail(feedId)
    }

    private lateinit var viewModel: FeedDetailAdminViewModel
    private lateinit var commentAdapter: CommentAdminAdapter

    private fun setupViewModel() {
        val dataSource = ManagementDataSource()
        val repository = ManagementRepository(dataSource)

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(FeedDetailAdminViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return FeedDetailAdminViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

        viewModel = ViewModelProvider(this, factory)[FeedDetailAdminViewModel::class.java]
    }

    private fun setupRecyclerView() {
        commentAdapter = CommentAdminAdapter(emptyList())

        binding.btnBack.setOnClickListener {
            view?.findNavController()?.navigateUp()
        }

        binding.layoutBottomInput.visibility = View.GONE

        binding.btnFeedOptions.setOnClickListener { view ->
            val popupMenu = androidx.appcompat.widget.PopupMenu(view.context, view)

            popupMenu.menuInflater.inflate(R.menu.menu_feed_actions, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_delete_item -> {
                        // TODO: Mở màn hình chỉnh sửa hoặc gọi callback báo cho Fragment biết
                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()

        }

        binding.rvComments.apply {
            adapter = commentAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        binding.btnSend.setOnClickListener {
            if (feedId == null) return@setOnClickListener

            val content = binding.etComment.text.toString().trim()

            if (content.isEmpty()) return@setOnClickListener


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
                    transformations(CircleCropTransformation())
                }

                binding.cbHeart.setOnCheckedChangeListener(null)
                binding.cbHeart.isChecked = item.isLiked
                binding.tvHeart.text = item.likeCount.toString()
                binding.tvCmt.text = item.commentCount.toString()
                binding.cbHeart.isEnabled = false

                binding.btnFeedOptions.setOnClickListener { view ->
                    val popupMenu = androidx.appcompat.widget.PopupMenu(view.context, view)

                    popupMenu.menuInflater.inflate(R.menu.menu_feed_actions, popupMenu.menu)

                    popupMenu.setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.action_delete_item -> {
                                // TODO: Mở màn hình chỉnh sửa hoặc gọi callback báo cho Fragment biết
                                true
                            }
                            else -> false
                        }
                    }

                    popupMenu.show()

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