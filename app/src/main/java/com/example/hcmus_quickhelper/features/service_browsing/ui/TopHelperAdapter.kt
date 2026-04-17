package com.example.hcmus_quickhelper.features.service_browsing.ui

import com.example.hcmus_quickhelper.features.service_browsing.model.Helper
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.hcmus_quickhelper.R

class TopHelperAdapter(
    private var helpers: List<Helper> = emptyList(),
    private val onClick: (Helper) -> Unit // Bắt sự kiện click chuyển trang
) : RecyclerView.Adapter<TopHelperAdapter.TopHelperViewHolder>() {

    fun updateData(newHelpers: List<Helper>) {
        helpers = newHelpers
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopHelperViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_top_helper_home, parent, false)
        return TopHelperViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopHelperViewHolder, position: Int) = holder.bind(helpers[position])
    override fun getItemCount() = helpers.size

    inner class TopHelperViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivAvatar: ImageView = itemView.findViewById(R.id.ivAvatar)
        private val tvHelperName: TextView = itemView.findViewById(R.id.tvHelperName)
        private val tvHelperRating: TextView = itemView.findViewById(R.id.tvHelperRating)
        private val btnViewProfile: Button = itemView.findViewById(R.id.btnViewProfile)

        fun bind(helper: Helper) {
            tvHelperName.text = helper.name
            tvHelperRating.text = "⭐ ${helper.rating}"

            ivAvatar.load(helper.avatarUrl) {
                placeholder(R.drawable.default_avt)
                error(R.drawable.default_avt)
            }

            // Click vào nút ViewProfile hoặc cả Card
            val clickAction = View.OnClickListener { onClick(helper) }
            btnViewProfile.setOnClickListener(clickAction)
            itemView.setOnClickListener(clickAction)
        }
    }
}