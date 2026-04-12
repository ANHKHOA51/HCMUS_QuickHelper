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

class BookingHistoryAdapter(
    private var histories: List<BookingHistory> = emptyList()
) : RecyclerView.Adapter<BookingHistoryAdapter.ViewHolder>() {

    fun updateData(newData: List<BookingHistory>) {
        histories = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(histories[position])
    }

    override fun getItemCount() = histories.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Ánh xạ View
        private val tvServiceName: TextView = itemView.findViewById(R.id.tvServiceName)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        private val tvPackageType: TextView = itemView.findViewById(R.id.tvPackageType)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)

        // Khai báo thêm CardView nền của Status
        private val cvStatusBackground: CardView = itemView.findViewById(R.id.cvStatusBackground)

        fun bind(item: BookingHistory) {
            tvServiceName.text = item.serviceName
            tvStatus.text = item.status
            tvPrice.text = item.priceText
            tvPackageType.text = item.packageType
            tvDate.text = item.date
            tvTime.text = item.time

            // Logic cấu trúc điều kiện (when) để thay đổi màu linh hoạt
            when (item.status) {
                "ĐANG THỰC HIỆN", "ĐÃ XÁC NHẬN" -> {
                    // Trạng thái: Xanh lá
                    tvStatus.setTextColor(Color.parseColor("#00C853"))
                    cvStatusBackground.setCardBackgroundColor(Color.parseColor("#B9F6CA"))
                }
                "ĐÃ HOÀN THÀNH" -> {
                    // Trạng thái: Xanh dương
                    tvStatus.setTextColor(Color.parseColor("#2196F3"))
                    cvStatusBackground.setCardBackgroundColor(Color.parseColor("#BBDEFB"))
                }
                "ĐÃ HỦY" -> {
                    // Trạng thái: Đỏ
                    tvStatus.setTextColor(Color.parseColor("#F44336"))
                    cvStatusBackground.setCardBackgroundColor(Color.parseColor("#FFCDD2"))
                }
                else -> {
                    // Trạng thái mặc định (Phòng hờ lỗi data): Xám
                    tvStatus.setTextColor(Color.parseColor("#616161"))
                    cvStatusBackground.setCardBackgroundColor(Color.parseColor("#E0E0E0"))
                }
            }
        }
    }
}