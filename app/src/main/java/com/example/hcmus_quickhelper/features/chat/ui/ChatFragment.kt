package com.example.hcmus_quickhelper.features.chat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import coil.load
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.databinding.FragmentChatBinding
import com.example.hcmus_quickhelper.features.chat.datasource.ChatRemoteDataSource
import com.example.hcmus_quickhelper.features.chat.repository.ChatRepository
import com.example.hcmus_quickhelper.features.chat.viewmodel.ChatViewModel

class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    val currentUserId = 5

    var conversationId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        observeViewModel()

        conversationId = arguments?.getInt("conversationId")

        if (conversationId != null) {
            viewModel.fetchMessage(conversationId!!)
            viewModel.subscribeMessages(conversationId!!)
        }
    }

    private lateinit var viewModel: ChatViewModel
    private lateinit var messageAdapter: MessageAdapter
    private fun setupViewModel() {
        val dataSource = ChatRemoteDataSource()
        val repository = ChatRepository(dataSource)

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return ChatViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

        viewModel = ViewModelProvider(this, factory)[ChatViewModel::class.java]
    }

    private fun setupRecyclerView() {
//        val senderId = arguments?.getInt("senderId")
        val senderName = arguments?.getString("senderName")
        val senderAvtUrl = arguments?.getString("senderAvtUrl")

        messageAdapter = MessageAdapter(emptyList(), currentUserId, senderAvtUrl)

        binding.ivAvatar.load(senderAvtUrl) {
            placeholder(R.drawable.default_avt)
            error(R.drawable.default_avt)
        }

        binding.tvChatName.text = senderName

        binding.recyclerViewChat.apply {
            adapter = messageAdapter
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        binding.btnBack.setOnClickListener {
            view?.findNavController()?.navigateUp()
        }

        binding.btnSend.setOnClickListener {
            if (conversationId == null) return@setOnClickListener

//            val conversationId = arguments?.getInt("conversationId") ?: return@setOnClickListener

            val content = binding.etMessage.text.toString().trim()

            if (content.isEmpty()) return@setOnClickListener

            viewModel.sendMessage(
                conversationId!!,
                currentUserId,
                content
            )

            binding.etMessage.text?.clear()
        }

        binding.recyclerViewChat.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom) {
                binding.recyclerViewChat.postDelayed({
                    val adapter = binding.recyclerViewChat.adapter
                    if (adapter != null && adapter.itemCount > 0) {
                        binding.recyclerViewChat.smoothScrollToPosition(adapter.itemCount - 1)
                    }
                }, 100)
            }
        }

    }

    private fun observeViewModel() {
        viewModel.messageList.observe(viewLifecycleOwner) { list ->
            messageAdapter.updateData(list)

            binding.recyclerViewChat.scrollToPosition(list.size - 1)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}