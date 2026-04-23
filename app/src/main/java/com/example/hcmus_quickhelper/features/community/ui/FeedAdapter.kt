package com.example.hcmus_quickhelper.features.community.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.core.utils.toRelativeTime
import com.example.hcmus_quickhelper.core.utils.toSmartTime
import com.example.hcmus_quickhelper.databinding.ItemPostBinding
import com.example.hcmus_quickhelper.features.community.model.Feed

class FeedAdapter (
    private var items: List<Feed>,
    private val currentUserId: Int,
    val onLikeClick: (Feed) -> Unit
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

        holder.binding.cbHeart.setOnCheckedChangeListener(null)
        holder.binding.cbHeart.isChecked = item.isLiked
        holder.binding.tvHeart.text = item.likeCount.toString()
        holder.binding.tvCmt.text = item.commentCount.toString()

        holder.binding.cbHeart.setOnClickListener {
            onLikeClick(item)
        }

        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("feedId", item.id)
            }

            holder.itemView.findNavController().navigate(R.id.action_community_to_feedDetail, bundle)
        }

    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newItems: List<Feed>) {
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = items.size
            override fun getNewListSize() = newItems.size
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return items[oldItemPosition].id == newItems[newItemPosition].id
            }
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return items[oldItemPosition] == newItems[newItemPosition]
            }
        })

        this.items = newItems.toList()
        diffResult.dispatchUpdatesTo(this)
    }
}