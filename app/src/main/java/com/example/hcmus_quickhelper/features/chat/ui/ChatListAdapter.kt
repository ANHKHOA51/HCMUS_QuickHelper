package com.example.hcmus_quickhelper.features.chat.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import android.view.View
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.core.utils.toSmartTime
import com.example.hcmus_quickhelper.databinding.ItemChatBinding
import com.example.hcmus_quickhelper.features.chat.model.ConversationItem
import androidx.recyclerview.widget.DiffUtil

class ChatListAdapter(
    private var items: List<ConversationItem>,
    private val currentUserId: Int,
    private val onClick: (ConversationItem) -> Unit
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
            onClick(item)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<ConversationItem>) {
        val diffCallback = object : DiffUtil.Callback() {

            override fun getOldListSize() = items.size
            override fun getNewListSize() = newItems.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return items[oldItemPosition].conversationId ==
                        newItems[newItemPosition].conversationId
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return items[oldItemPosition] == newItems[newItemPosition]
            }
        }

        val diffResult = DiffUtil.calculateDiff(diffCallback)

        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }
}