package com.example.hcmus_quickhelper.features.booking.ui

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.core.utils.toSmartTime
import com.example.hcmus_quickhelper.databinding.ItemBookingRequestBinding
import com.example.hcmus_quickhelper.features.booking.model.BookingRequest
import com.example.hcmus_quickhelper.features.booking.model.BookingStatus
import com.example.hcmus_quickhelper.features.booking.viewmodel.BookingRequestTab

class BookingRequestAdapter (
    private var bookings: List<BookingRequest> = emptyList(),
) : RecyclerView.Adapter<BookingRequestAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemBookingRequestBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(bookings[position])
    }

    override fun getItemCount(): Int {
        return bookings.size
    }

    fun updateData(newList: List<BookingRequest>) {
        bookings = newList
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemBookingRequestBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BookingRequest) {
            binding.tvCustomerName.text = item.customerName
            binding.tvServiceName.text = item.serviceName
            binding.tvDateBooking.text = item.schedule.toSmartTime()
            binding.tvAddress.text = item.address
            
            // Handle possible enum mapping errors safely
            binding.tvStatus.text = try {
                BookingStatus.valueOf(item.status).value
            } catch (e: Exception) {
                item.status
            }

            val context = binding.tvStatus.context

            val (bgColor, textColor) = when (item.status) {
                BookingStatus.PENDING.toString() -> {
                    ContextCompat.getColor(context, R.color.gray_light) to ContextCompat.getColor(context, R.color.gray_hint)
                }
                BookingStatus.CONFIRMED.toString() -> {
                    ContextCompat.getColor(context, R.color.green_light) to ContextCompat.getColor(context, R.color.green)
                }
                BookingStatus.COMPLETED.toString() -> {
                    ContextCompat.getColor(context, R.color.orange_light) to ContextCompat.getColor(context, R.color.orange_primary)
                }
                else -> {
                    ContextCompat.getColor(context, R.color.blue_light_300) to ContextCompat.getColor(context, R.color.blue)
                }
            }

            binding.tvStatus.backgroundTintList = ColorStateList.valueOf(bgColor)
            binding.tvStatus.setTextColor(textColor)
        }
    }
}
