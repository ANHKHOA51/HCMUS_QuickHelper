package com.example.hcmus_quickhelper.features.voucher.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.features.voucher.model.Voucher

class VoucherAdapter (
    private var vouchers: List<Voucher> = emptyList(),
    private val onVoucherClick: (Voucher) -> Unit
): RecyclerView.Adapter<VoucherAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_voucher, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(vouchers[position])
    }

    override fun getItemCount(): Int {
        return vouchers.size
    }

    fun updateData(newList: List<Voucher>) {
        vouchers = newList
        notifyDataSetChanged() // Thông báo cho RecyclerView vẽ lại
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivBanner = itemView.findViewById<ImageView>(R.id.ivBanner)
        val txName = itemView.findViewById<TextView>(R.id.tvName)
        val tvDescription = itemView.findViewById<TextView>(R.id.tvDescription)
        val tvExpire = itemView.findViewById<TextView>(R.id.tvExpire)

        fun bind(voucher: Voucher) {
            txName.text = voucher.name
            tvDescription.text = voucher.description
            tvExpire.text = voucher.expiredAt
        }
    }

    class VoucherDiffCallback : DiffUtil.ItemCallback<Voucher>() {
        override fun areItemsTheSame(oldItem: Voucher, newItem: Voucher): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Voucher, newItem: Voucher): Boolean {
            return oldItem == newItem
        }
    }
}