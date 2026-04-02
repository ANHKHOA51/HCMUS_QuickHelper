package com.example.hcmus_quickhelper.features.service_browsing.ui

import android.graphics.Color
import android.view.*
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.features.service_browsing.model.Voucher

class VoucherAdapter(private var vouchers: List<Voucher> = emptyList()) : RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder>() {

    fun updateData(newVouchers: List<Voucher>) {
        vouchers = newVouchers
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoucherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_voucher_home, parent, false)
        return VoucherViewHolder(view)
    }

    override fun onBindViewHolder(holder: VoucherViewHolder, position: Int) = holder.bind(vouchers[position])
    override fun getItemCount() = vouchers.size

    inner class VoucherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cvVoucherContainer: CardView = itemView.findViewById(R.id.cvVoucherContainer)
        private val tvVoucherCode: TextView = itemView.findViewById(R.id.tvVoucherCode)
        private val tvVoucherTitle: TextView = itemView.findViewById(R.id.tvVoucherTitle)

        fun bind(voucher: Voucher) {
            tvVoucherCode.text = voucher.code
            tvVoucherTitle.text = voucher.title
            cvVoucherContainer.setCardBackgroundColor(Color.parseColor(voucher.colorHex))
        }
    }
}