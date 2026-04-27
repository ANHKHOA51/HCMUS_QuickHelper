package com.example.hcmus_quickhelper.features.admin_management.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.core.utils.toRelativeTime
import com.example.hcmus_quickhelper.databinding.ItemPostBinding
import com.example.hcmus_quickhelper.features.community.model.Feed

class FeedAdminAdapter (
    private var items: List<Feed>,
    private val onDeleteClick: (feedId: Int) -> Unit
) : RecyclerView.Adapter<FeedAdminAdapter.CommunityViewHolder>() {

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
        holder.binding.cbHeart.isEnabled = false
        holder.binding.tvHeart.text = item.likeCount.toString()
        holder.binding.tvCmt.text = item.commentCount.toString()

        holder.binding.btnFeedOptions.setOnClickListener { view ->
            val popupMenu = androidx.appcompat.widget.PopupMenu(view.context, view)

            popupMenu.menuInflater.inflate(R.menu.menu_feed_actions, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_delete_item -> {
                        onDeleteClick.invoke(item.id)
                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()
        }

        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("feedId", item.id)
            }

            holder.itemView.findNavController().navigate(R.id.action_admin_feed_to_admin_feed_detail, bundle)
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