package com.example.hcmus_quickhelper.features.chat.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.features.chat.model.Message           // Dùng đúng model này
import com.example.hcmus_quickhelper.core.utils.toSmartTime
import com.example.hcmus_quickhelper.databinding.ItemMessageIncomingBinding
import com.example.hcmus_quickhelper.databinding.ItemMessageOutgoingBinding

class MessageAdapter(
    private var items: List<Message>,
    private val currentUserId: Int,
    private val senderAvtUrl: String?,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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

        if (holder is SentViewHolder) {
            holder.bind(message)
        } else if (holder is ReceivedViewHolder) {
            holder.bind(message)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class SentViewHolder(private val binding: ItemMessageOutgoingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.tvMessage.text = message.message
            binding.tvTime.text = message.createdAt.toSmartTime()
        }
    }

    inner class ReceivedViewHolder(private val binding: ItemMessageIncomingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.tvMessage.text = message.message
            binding.tvTime.text = message.createdAt.toSmartTime()
            binding.ivAvatar.load(senderAvtUrl) {
                placeholder(R.drawable.default_avt)
                error(R.drawable.default_avt)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newMessages: List<Message>) {
        this.items = newMessages
        notifyDataSetChanged()
    }
}