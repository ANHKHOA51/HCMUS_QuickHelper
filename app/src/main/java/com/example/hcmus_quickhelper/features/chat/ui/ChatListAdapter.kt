package com.example.hcmus_quickhelper.features.chat.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import android.view.View
import androidx.navigation.findNavController
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.core.utils.toSmartTime
import com.example.hcmus_quickhelper.databinding.ItemChatBinding
import com.example.hcmus_quickhelper.features.chat.model.ConversationItem

class ChatListAdapter(
    private var items: List<ConversationItem>,
    private val currentUserId: Int
) : RecyclerView.Adapter<ChatListAdapter.ChatViewHolder>() {

    class ChatViewHolder(val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val item = items[position]

        val avtUrl: String?

        if (item.customerId == currentUserId) {
            holder.binding.tvName.text = item.helperName
            avtUrl = item.helperAvt
        } else if (item.helperId == currentUserId) {
            holder.binding.tvName.text = item.customerName
            avtUrl = item.customerAvt
        } else {
            holder.binding.tvName.text = "Unknown"
            avtUrl = null
        }

        holder.binding.ivAvatar.load(avtUrl) {
            placeholder(R.drawable.default_avt)
            error(R.drawable.default_avt)
        }

        holder.binding.tvLastMessage.text = if (item.latestMessage.length > 35)
            "${item.latestMessage.take(35)}..." else item.latestMessage

        holder.binding.tvTime.text = item.lastMessageTime.toSmartTime()

        if (item.isRead) {
            holder.binding.ivUnreadBadge.visibility = View.GONE
        } else {
            holder.binding.ivUnreadBadge.visibility = View.VISIBLE
        }

        holder.itemView.setOnClickListener {
            var senderAvtUrl: String?
            var senderName: String?

            if (item.customerId == currentUserId) {
                senderName = item.helperName
                senderAvtUrl = item.helperAvt
            } else if (item.helperId == currentUserId) {
                senderName = item.customerName
                senderAvtUrl = item.customerAvt
            } else {
                senderName = "Unknown"
                senderAvtUrl = null
            }

            val bundle = Bundle().apply {
                putInt("senderId", item.senderId)
                putString("senderName", senderName)
                putString("senderAvtUrl", senderAvtUrl)
                putInt("conversationId", item.conversationId)
            }

            holder.itemView.findNavController().navigate(R.id.action_chatList_to_chat, bundle)
        }
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newItems: List<ConversationItem>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}