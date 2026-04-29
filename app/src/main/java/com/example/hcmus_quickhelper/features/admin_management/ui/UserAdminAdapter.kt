package com.example.hcmus_quickhelper.features.admin_management.ui

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
import com.example.hcmus_quickhelper.core.model.User
import com.example.hcmus_quickhelper.core.utils.toRelativeTime
import com.example.hcmus_quickhelper.databinding.ItemPostBinding
import com.example.hcmus_quickhelper.databinding.ItemUserBinding
import com.example.hcmus_quickhelper.features.community.model.Feed

class UserAdminAdapter (
    private var items: List<User>,
    private val onResetPasswordClick: (userId: Int, userEmail: String) -> Unit,
    private val onWarningClick: (userEmail: String) -> Unit,
    private val onBlockClick: (userId: Int, isBlocked: Boolean) -> Unit
) : RecyclerView.Adapter<UserAdminAdapter.UserViewHolder>() {

    class UserViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val item = items[position]

        holder.binding.tvUserFullName.text = item.fullname

        holder.binding.tvUserRole.text = '#' + item.role

        holder.binding.tvUserPhone.text = item.phone

        holder.binding.tvUserEmail.text = item.email

        if (item.rating != null) {
            holder.binding.tvUserRating.text = item.rating.toString()
        } else {
            holder.binding.tvUserRating.visibility = View.GONE
        }

        holder.binding.ivUserAvatar.load(item.avatarUrl) {
            placeholder(R.drawable.default_avt)
            error(R.drawable.default_avt)
        }

        if (!item.isBlocked) {
            holder.binding.tvUserBlock.visibility = View.GONE
        } else {
            holder.binding.tvUserBlock.visibility = View.VISIBLE
        }

        holder.binding.btnUserOptions.setOnClickListener { view ->
            val popupMenu = androidx.appcompat.widget.PopupMenu(view.context, view)

            popupMenu.menuInflater.inflate(R.menu.menu_user_actions, popupMenu.menu)

            val lockItem = popupMenu.menu.findItem(R.id.action_block_user)
            if (item.isBlocked) {
                lockItem.title = "Mở khóa tài khoản"
            } else {
                lockItem.title = "Khóa tài khoản"
            }
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_reset_password_user -> {
                        onResetPasswordClick.invoke(item.id, item.email)
                        true
                    }
                    R.id.action_waring_user -> {
                        onWarningClick.invoke(item.email)
                        true
                    }
                    R.id.action_block_user -> {
                        onBlockClick.invoke(item.id, item.isBlocked)
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
    fun updateData(newItems: List<User>) {
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