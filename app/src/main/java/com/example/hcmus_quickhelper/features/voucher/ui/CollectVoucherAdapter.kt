package com.example.hcmus_quickhelper.features.voucher.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hcmus_quickhelper.core.utils.toSmartTime
import com.example.hcmus_quickhelper.databinding.ItemCollectVoucherBinding
import com.example.hcmus_quickhelper.databinding.ItemVoucherBinding
import com.example.hcmus_quickhelper.features.voucher.model.Voucher
import java.text.SimpleDateFormat
import java.util.Locale

class CollectVoucherAdapter(
    private var vouchers: List<Voucher> = emptyList(),
    var onCollectVoucher: (Voucher) -> Unit
) : RecyclerView.Adapter<CollectVoucherAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemCollectVoucherBinding.inflate(
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
        val voucher = vouchers[position]
        holder.bind(voucher)
    }

    override fun getItemCount(): Int {
        return vouchers.size
    }

    fun updateData(newList: List<Voucher>) {
        vouchers = newList
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemCollectVoucherBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(voucher: Voucher) {
            binding.apply {
                binding.tvCode.text = voucher.code
                binding.tvExpire.text = voucher.expiredAt.toSmartTime()

                binding.btnCollect.setOnClickListener { onCollectVoucher(voucher) }
            }
        }
    }
}