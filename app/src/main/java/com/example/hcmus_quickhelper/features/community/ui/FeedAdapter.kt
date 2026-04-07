package com.example.hcmus_quickhelper.features.community.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.core.utils.toRelativeTime
import com.example.hcmus_quickhelper.core.utils.toSmartTime
import com.example.hcmus_quickhelper.databinding.ItemPostBinding
import com.example.hcmus_quickhelper.features.community.model.Feed

class FeedAdapter (
    private var items: List<Feed>,
    private val currentUserId: Int
) : RecyclerView.Adapter<FeedAdapter.CommunityViewHolder>() {

    class CommunityViewHolder(val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommunityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommunityViewHolder, position: Int) {
        val item = items[position]


        holder.binding.tvName.text = item.ownerFullname

        holder.binding.tvHandle.text = '@' + item.ownerUsername

        holder.binding.tvContent.text = item.content

        holder.binding.tvTag.text = '#' + item.ownerRole.lowercase()

        holder.binding.tvTime.text = item.createdAt.toRelativeTime()

        holder.binding.ivAvatar.load(item.ownerAvatarUrl) {
            placeholder(R.drawable.default_avt)
            error(R.drawable.default_avt)
        }

        if (item.isLiked) {
            holder.binding.ivHeart.setImageResource(R.drawable.ic_heart_filled)
        }

        holder.binding.tvHeart.text = item.likeCount.toString()
        holder.binding.tvCmt.text = item.commentCount.toString()

//        holder.itemView.setOnClickListener {
//
//            }

//            holder.itemView.findNavController().navigate()
//        }
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newItems: List<Feed>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}