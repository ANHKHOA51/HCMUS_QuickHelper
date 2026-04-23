package com.example.hcmus_quickhelper.features.voucher.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hcmus_quickhelper.core.utils.MoneyUtils
import com.example.hcmus_quickhelper.core.utils.toSmartTime
import com.example.hcmus_quickhelper.databinding.ItemVoucherAdminBinding
import com.example.hcmus_quickhelper.features.voucher.model.Voucher

class VoucherAdminAdapter(
    private var vouchers: List<Voucher> = emptyList(),
    private val onEditClick: (Voucher) -> Unit,
    private val onDeleteClick: (Voucher) -> Unit
) : RecyclerView.Adapter<VoucherAdminAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVoucherAdminBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(vouchers[position])
    }

    override fun getItemCount(): Int = vouchers.size

    fun updateData(newList: List<Voucher>) {
        vouchers = newList
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemVoucherAdminBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(voucher: Voucher) {
            binding.apply {
                tvVoucherCode.text = voucher.code
                tvVoucherDiscount.text = "Giảm ${MoneyUtils.formatVietnameseCurrency(voucher.discount)}"
                tvVoucherInfo.text = "SL: ${voucher.quantity} | HSD: ${voucher.expiredAt.toSmartTime()}"

                btnEdit.setOnClickListener { onEditClick(voucher) }
                btnDelete.setOnClickListener { onDeleteClick(voucher) }
            }
        }
    }
}
