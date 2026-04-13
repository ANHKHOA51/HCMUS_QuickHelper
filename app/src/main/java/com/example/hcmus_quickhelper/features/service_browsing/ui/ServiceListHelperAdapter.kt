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

class ServiceListHelperAdapter (
    private var helpers: List<Helper> = emptyList(),
    private val onBookClick: (Helper) -> Unit
) : RecyclerView.Adapter<ServiceListHelperAdapter.HelperViewHolder>(){

    fun updateData(newHelpers: List<Helper>){
        helpers = newHelpers
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelperViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expert_fservicelist, parent, false)
        return HelperViewHolder(view)
    }

    override fun onBindViewHolder(holder: HelperViewHolder, position: Int) {
        holder.bind(helpers[position])
    }

    override fun getItemCount(): Int {
        return helpers.size
    }

    inner class HelperViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvExpertName: TextView = itemView.findViewById(R.id.tvExpertName)
        private val tvRating: TextView = itemView.findViewById(R.id.tvRating)
        private val tvSkills: TextView = itemView.findViewById(R.id.tvSkills)
        private val tvDistance: TextView = itemView.findViewById(R.id.tvDistance)
        private val tvReviewCount: TextView = itemView.findViewById(R.id.tvReviewCount)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        private val ivOnlineStatus: ImageView = itemView.findViewById(R.id.ivOnlineStatus)
        private val ivVerified: ImageView = itemView.findViewById(R.id.ivVerified)
        private val btnBook: Button = itemView.findViewById(R.id.btnBook)

        fun bind(helper: Helper) {
            tvExpertName.text = helper.name
            tvRating.text = helper.rating.toString()
            tvSkills.text = helper.skills
            tvDistance.text = "${helper.distance} km"
            tvReviewCount.text = "${helper.reviewCount} đánh giá"
            tvPrice.text = helper.priceText

            // Xử lý hiển thị các icon trạng thái
            ivOnlineStatus.visibility = if (helper.isOnline) View.VISIBLE else View.GONE
            ivVerified.visibility = if (helper.isVerified) View.VISIBLE else View.GONE

            btnBook.setOnClickListener {
                onBookClick(helper)
            }
        }
    }

}