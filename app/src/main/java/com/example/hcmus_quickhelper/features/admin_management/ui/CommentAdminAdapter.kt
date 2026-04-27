package com.example.hcmus_quickhelper.features.admin_management.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.core.utils.toRelativeTime
import com.example.hcmus_quickhelper.databinding.ItemCommentBinding
import com.example.hcmus_quickhelper.features.community.model.CommentUI

class CommentAdminAdapter  (
    private var items: List<CommentUI>,
) : RecyclerView.Adapter<CommentAdminAdapter.CommentViewHolder>() {

    class CommentViewHolder(val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val item = items[position]

        if (!item.commentContent.isNullOrBlank()) {
            // Hiển thị toàn bộ item (phòng trường hợp item trước đó bị GONE)
            holder.itemView.visibility = View.VISIBLE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            holder.binding.tvCommentName.text = item.commentorName
            holder.binding.tvCommentTime.text = item.commentTime.toRelativeTime()
            holder.binding.tvCommentContent.text = item.commentContent

            holder.binding.ivCommentAvatar.load(item.commentorAvt) {
                crossfade(true)
                placeholder(R.drawable.default_avt)
                error(R.drawable.default_avt)
                transformations(CircleCropTransformation())
            }
        } else {
            // Nếu không có bình luận, ẩn toàn bộ dòng này đi để không để lại khoảng trắng
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
        }

        holder.itemView.setOnClickListener {view ->
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

    override fun getItemCount(): Int = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newItems: List<CommentUI>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}