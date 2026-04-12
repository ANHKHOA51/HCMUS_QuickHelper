package com.example.hcmus_quickhelper.features.chat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.example.hcmus_quickhelper.features.chat.datasource.ChatRemoteDataSource
import com.example.hcmus_quickhelper.features.chat.repository.ChatRepository
import com.example.hcmus_quickhelper.features.chat.viewmodel.ConversationViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hcmus_quickhelper.databinding.FragmentChatListBinding
import androidx.navigation.findNavController
import com.example.hcmus_quickhelper.R

class ChatListFragment : Fragment() {
    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        observeViewModel()

        viewModel.fetchConversations(3)
        viewModel.subscribeAllMessages()
    }

    private lateinit var viewModel: ConversationViewModel
    private lateinit var chatAdapter: ChatListAdapter

    private fun setupViewModel() {
        val dataSource = ChatRemoteDataSource()
        val repository = ChatRepository(dataSource)

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ConversationViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return ConversationViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

        viewModel = ViewModelProvider(this, factory)[ConversationViewModel::class.java]
    }

    private fun setupRecyclerView() {
        val currentUserId = 3

        chatAdapter = ChatListAdapter(
            emptyList(),
            currentUserId
        ) { item ->

            val senderName: String
            val senderAvtUrl: String?

            if (item.customerId == currentUserId) {
                senderName = item.helperName
                senderAvtUrl = item.helperAvt
            } else {
                senderName = item.customerName
                senderAvtUrl = item.customerAvt
            }

            val bundle = Bundle().apply {
                putInt("conversationId", item.conversationId)
                putInt("senderId", item.senderId)
                putString("senderName", senderName)
                putString("senderAvtUrl", senderAvtUrl)
            }

            view?.findNavController()
                ?.navigate(R.id.action_chatList_to_chat, bundle)
        }

        binding.rvMessages.apply {
            adapter = chatAdapter
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        viewModel.conversationList.observe(viewLifecycleOwner) { list ->
            chatAdapter.updateData(list)

            binding.rvMessages.scrollToPosition(0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

