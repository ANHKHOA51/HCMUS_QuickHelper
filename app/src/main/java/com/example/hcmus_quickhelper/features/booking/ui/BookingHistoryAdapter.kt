package com.example.hcmus_quickhelper.features.booking.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.hcmus_quickhelper.features.booking.model.BookingHistory
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.core.model.BookingStatus
import com.example.hcmus_quickhelper.databinding.ItemBookingHistoryBinding

class BookingHistoryAdapter(
    private var histories: List<BookingHistory> = emptyList(),
    private val onItemClick: (Int, String) -> Unit
) : RecyclerView.Adapter<BookingHistoryAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemBookingHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    fun updateData(newData: List<BookingHistory>) {
        histories = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBookingHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = histories[position]

        holder.binding.tvServiceName.text = item.serviceName
        holder.binding.tvPrice.text = item.priceText
        holder.binding.tvPackageType.text = item.packageType
        holder.binding.tvDate.text = item.date
        holder.binding.tvTime.text = item.time

        // Hiển thị tiếng Việt ra UI
        holder.binding.tvStatus.text = item.statusEnum.value

        // Logic đổi màu dựa theo Enum
        when (item.statusEnum) {
            BookingStatus.PENDING -> {
                holder.binding.tvStatus.setTextColor(Color.parseColor("#E65100"))
                holder.binding.cvStatusBackground.setCardBackgroundColor(Color.parseColor("#FFF3E0"))
            }
            BookingStatus.IN_PROGRESS, BookingStatus.CONFIRMED -> {
                holder.binding.tvStatus.setTextColor(Color.parseColor("#1565C0"))
                holder.binding.cvStatusBackground.setCardBackgroundColor(Color.parseColor("#E3F2FD"))
            }
            BookingStatus.COMPLETED -> {
                holder.binding.tvStatus.setTextColor(Color.parseColor("#2E7D32"))
                holder.binding.cvStatusBackground.setCardBackgroundColor(Color.parseColor("#E8F5E9"))
            }
            BookingStatus.REJECTED -> {
                holder.binding.tvStatus.setTextColor(Color.parseColor("#D32F2F"))
                holder.binding.cvStatusBackground.setCardBackgroundColor(Color.parseColor("#FFEBEE"))
            }
        }

        holder.itemView.setOnClickListener {
            onItemClick(item.id, item.serviceName)
        }
    }

    override fun getItemCount() = histories.size
}