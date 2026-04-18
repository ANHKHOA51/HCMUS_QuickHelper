package com.example.hcmus_quickhelper.features.service_browsing.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.features.service_browsing.model.Helper
import com.example.hcmus_quickhelper.databinding.ItemExpertFservicelistBinding
import coil.load
import androidx.recyclerview.widget.DiffUtil

class ServiceListHelperAdapter(
    private var helpers: List<Helper>,
    private val onBookClick: (Helper) -> Unit
) : RecyclerView.Adapter<ServiceListHelperAdapter.HelperViewHolder>() {

    class HelperViewHolder(val binding: ItemExpertFservicelistBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelperViewHolder {
        val binding = ItemExpertFservicelistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HelperViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HelperViewHolder, position: Int) {
        val helper = helpers[position]

        with(holder.binding) {
            tvExpertName.text = helper.name
            tvRating.text = helper.rating.toString()
            tvSkills.text = helper.skills
            if (helper.price > 0.0) {
                tvPrice.text = "Từ ${helper.price.toLong()}đ/giờ"
            } else {
                tvPrice.text = "Liên hệ"
            }

            // Load Avatar bằng Coil
            ivAvatar.load(helper.avatarUrl) {
                placeholder(R.drawable.default_avt)
                error(R.drawable.default_avt)
            }

            ivOnlineStatus.visibility = if (helper.isOnline) View.VISIBLE else View.GONE

            btnBook.setOnClickListener {
                onBookClick(helper)
            }
        }
    }

    override fun getItemCount(): Int = helpers.size

    fun updateData(newHelpers: List<Helper>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize() = helpers.size
            override fun getNewListSize() = newHelpers.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return helpers[oldItemPosition].id == newHelpers[newItemPosition].id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return helpers[oldItemPosition] == newHelpers[newItemPosition]
            }
        }

        val diffResult = DiffUtil.calculateDiff(diffCallback)
        helpers = newHelpers
        diffResult.dispatchUpdatesTo(this)
    }
}