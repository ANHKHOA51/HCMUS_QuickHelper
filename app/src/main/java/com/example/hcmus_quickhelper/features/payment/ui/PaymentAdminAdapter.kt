package com.example.hcmus_quickhelper.features.payment.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.core.utils.MoneyUtils
import com.example.hcmus_quickhelper.core.utils.toSmartTime
import com.example.hcmus_quickhelper.databinding.ItemPaymentAdminBinding
import com.example.hcmus_quickhelper.features.payment.model.Payment
import com.example.hcmus_quickhelper.features.payment.model.PaymentStatus

class PaymentAdminAdapter(
    private var payments: List<Payment> = emptyList(),
    private val onItemClick: (Payment) -> Unit
) : RecyclerView.Adapter<PaymentAdminAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPaymentAdminBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(payments[position])
    }

    override fun getItemCount(): Int = payments.size

    fun updateData(newList: List<Payment>) {
        payments = newList
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemPaymentAdminBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(payment: Payment) {
            binding.apply {
                tvPaymentId.text = "#HD${payment.id}"
                tvServiceName.text = payment.booking?.service?.name ?: "Dịch vụ"
                tvAmount.text = MoneyUtils.formatVietnameseCurrency(payment.amount)
                tvDate.text = payment.createdAt?.toSmartTime() ?: ""

                tvStatus.text = when (payment.status) {
                    PaymentStatus.SUCCESS.toString() -> "Thành công"
                    PaymentStatus.PENDING.toString() -> "Chờ xử lý"
                    PaymentStatus.FAILED.toString() -> "Thất bại"
                    PaymentStatus.CANCELED.toString() -> "Đã hủy"
                    else -> payment.status
                }

                val context = tvStatus.context
                val statusColor = when (payment.status) {
                    PaymentStatus.SUCCESS.toString() -> R.color.green
                    PaymentStatus.PENDING.toString() -> R.color.blue
                    else -> R.color.red
                }
                tvStatus.setTextColor(ContextCompat.getColor(context, statusColor))

                root.setOnClickListener { onItemClick(payment) }
            }
        }
    }
}
