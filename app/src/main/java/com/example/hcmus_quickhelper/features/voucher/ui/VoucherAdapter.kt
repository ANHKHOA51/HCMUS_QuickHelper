package com.example.hcmus_quickhelper.features.voucher.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hcmus_quickhelper.databinding.ItemVoucherBinding
import com.example.hcmus_quickhelper.features.voucher.model.Voucher

class VoucherAdapter(
    private var vouchers: List<Voucher> = emptyList(),
    var onVoucherClick: (Voucher) -> Unit
) : RecyclerView.Adapter<VoucherAdapter.ViewHolder>() {

    private var selectedVoucherId: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVoucherBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val voucher = vouchers[position]
        holder.bind(voucher, voucher.id == selectedVoucherId)
    }

    override fun getItemCount(): Int = vouchers.size

    fun updateData(newList: List<Voucher>) {
        vouchers = newList
        notifyDataSetChanged()
    }

    fun updateSelectedVoucher(id: String) {
        val oldSelectedId = selectedVoucherId
        selectedVoucherId = id

        vouchers.forEachIndexed { index, voucher ->
            if (voucher.id == oldSelectedId || voucher.id == id) {
                notifyItemChanged(index)
            }
        }
    }

    inner class ViewHolder(private val binding: ItemVoucherBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(voucher: Voucher, isSelected: Boolean) {
            binding.apply {
                tvName.text = voucher.name
                tvDescription.text = voucher.description
                tvExpire.text = voucher.expiredAt

                container.isSelected = isSelected

                ivCheck.visibility = if (isSelected) View.VISIBLE else View.GONE

                root.setOnClickListener {
                    updateSelectedVoucher(voucher.id)
                    onVoucherClick(voucher)
                }
            }
        }
    }
}