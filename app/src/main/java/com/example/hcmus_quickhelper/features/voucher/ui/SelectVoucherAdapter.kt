package com.example.hcmus_quickhelper.features.voucher.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hcmus_quickhelper.core.utils.toSmartTime
import com.example.hcmus_quickhelper.databinding.ItemVoucherBinding
import com.example.hcmus_quickhelper.features.voucher.model.Voucher
import java.text.SimpleDateFormat
import java.util.Locale

class SelectVoucherAdapter(
    private var vouchers: List<Voucher> = emptyList(),
    var onVoucherClick: (Voucher) -> Unit
) : RecyclerView.Adapter<SelectVoucherAdapter.ViewHolder>() {

    private var selectedVoucherId: Int? = null

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

    fun updateSelectedVoucher(id: Int) {
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
                tvCode.text = voucher.code
                tvExpire.text = voucher.expiredAt.toSmartTime()

                container.isSelected = isSelected

                ivCheck.visibility = if (isSelected) View.VISIBLE else View.GONE

                root.setOnClickListener {
                    updateSelectedVoucher(voucher.id)
                    onVoucherClick(voucher)
                }
            }
        }

        private fun formatTime(time: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val outputFormat = SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault())

                val date = inputFormat.parse(time)
                if (date != null) outputFormat.format(date) else time
            } catch (e: Exception) {
                e.printStackTrace()
                time
            }
        }
    }
}