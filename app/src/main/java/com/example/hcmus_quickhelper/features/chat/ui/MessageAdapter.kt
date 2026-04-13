package com.example.hcmus_quickhelper.features.chat.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.features.chat.model.Message
import com.example.hcmus_quickhelper.core.utils.toSmartTime
import com.example.hcmus_quickhelper.databinding.ItemMessageIncomingBinding
import com.example.hcmus_quickhelper.databinding.ItemMessageOutgoingBinding
import androidx.recyclerview.widget.DiffUtil
import com.example.hcmus_quickhelper.core.utils.toMessageTime

class MessageAdapter(
    private var items: List<Message>,
    private val currentUserId: Int,
    private val senderAvtUrl: String?,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return items[position].messageId.toLong()
    }

    private val VIEW_TYPE_SENT = 1
    private val VIEW_TYPE_RECEIVED = 2

    override fun getItemViewType(position: Int): Int {
        return if (items[position].senderId == currentUserId) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            val binding = ItemMessageOutgoingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            SentViewHolder(binding)
        } else {
            val binding = ItemMessageIncomingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ReceivedViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = items[position]

        var showTime = true

        if (position < items.size - 1) {
            val nextMessage = items[position + 1]

            if (message.createdAt.toMessageTime() == nextMessage.createdAt.toMessageTime()) {
                showTime = false
            }
        }

        // Truyền cờ showTime vào hàm bind
        if (holder is SentViewHolder) {
            holder.bind(message, showTime)
        } else if (holder is ReceivedViewHolder) {
            holder.bind(message, showTime)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class SentViewHolder(private val binding: ItemMessageOutgoingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message, showTime: Boolean) {
            binding.tvMessage.text = message.message
            binding.tvTime.text = message.createdAt.toMessageTime()

            binding.tvTime.visibility = if (showTime) View.VISIBLE else View.GONE

            val marginInDp = if (showTime) 8 else 2
            val marginInPx = (marginInDp * binding.root.context.resources.displayMetrics.density).toInt()

            binding.layoutRoot.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = marginInPx
            }
        }
    }

    inner class ReceivedViewHolder(private val binding: ItemMessageIncomingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message, showTime: Boolean) {
            binding.tvMessage.text = message.message
            binding.tvTime.text = message.createdAt.toMessageTime()

            binding.tvTime.visibility = if (showTime) View.VISIBLE else View.GONE
            val marginInDp = if (showTime) 8 else 2
            val marginInPx = (marginInDp * binding.root.context.resources.displayMetrics.density).toInt()

            binding.layoutRoot.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = marginInPx
            }

            binding.ivAvatar.load(senderAvtUrl) {
                placeholder(R.drawable.default_avt)
                error(R.drawable.default_avt)
            }
        }
    }

    fun updateData(newMessages: List<Message>) {
        val oldSize = items.size

        val diffCallback = object : DiffUtil.Callback() {

            override fun getOldListSize() = items.size
            override fun getNewListSize() = newMessages.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return items[oldItemPosition].messageId ==
                        newMessages[newItemPosition].messageId
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return items[oldItemPosition] == newMessages[newItemPosition]
            }
        }

        val diffResult = DiffUtil.calculateDiff(diffCallback)

        items = newMessages
        diffResult.dispatchUpdatesTo(this)

        if (oldSize > 0 && newMessages.size > oldSize) {
            // Ép Adapter vẽ lại cái tin nhắn cuối cùng của danh sách cũ
            notifyItemChanged(oldSize - 1)
        }
    }
}