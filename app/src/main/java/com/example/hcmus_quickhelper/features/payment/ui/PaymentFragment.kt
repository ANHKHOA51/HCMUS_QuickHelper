package com.example.hcmus_quickhelper.features.payment.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.features.payment.viewmodel.PaymentViewModel
import kotlinx.datetime.LocalDateTime


class PaymentFragment : Fragment(R.layout.fragment_payment) {

    private val viewModel: PaymentViewModel by lazy {
        ViewModelProvider(this)[PaymentViewModel::class.java]
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvServiceName = view.findViewById<TextView>(R.id.tvServiceName)
        val tvHelperName = view.findViewById<TextView>(R.id.tvHelperName)

        val payment = viewModel.payment.value
        tvServiceName.text = payment.service
        tvHelperName.text = payment.helper

        val tvDateBooking = view.findViewById<TextView>(R.id.tvDateBooking)
        val tvTimeBooking = view.findViewById<TextView>(R.id.tvTimeBooking)
        val tvAddressBooking = view.findViewById<TextView>(R.id.tvAddressBooking)

        val datetime = LocalDateTime.parse(payment.createdAt)

        tvDateBooking.text = datetime.date.toString()
        tvTimeBooking.text = datetime.time.toString()
        tvAddressBooking.text = payment.address

        val layoutVoucherPicker = view.findViewById<View>(R.id.layoutVoucherPicker)

        layoutVoucherPicker.setOnClickListener {
            Log.d("PaymentFragment", "Voucher picker clicked")
        }

        val tvVoucherName = view.findViewById<TextView>(R.id.tvVoucherName)
        val tvVoucherItemDiscount = view.findViewById<TextView>(R.id.tvVoucherItemDiscount)

        tvVoucherName.text = "QUICKFREESHIP"
        tvVoucherItemDiscount.text = "giảm 35.0000đ"

        val btnCancelVoucher = view.findViewById<View>(R.id.btnCancelVoucher)
        btnCancelVoucher.setOnClickListener {
            Log.d("PaymentFragment", "Cancel voucher clicked")
        }

        val tvServicePrice = view.findViewById<TextView>(R.id.tvServicePrice)
        val tvPlatformPrice = view.findViewById<TextView>(R.id.tvPlatformPrice)
        val tvInsurancePrice = view.findViewById<TextView>(R.id.tvInsurancePrice)
        val tvVoucherDiscount = view.findViewById<TextView>(R.id.tvVoucherDiscount)

        tvServicePrice.text = "250.000đ"
        tvPlatformPrice.text = "15.000đ"
        tvInsurancePrice.text = "5.000đ"
        tvVoucherDiscount.text = "-35.000đ"

        val tvTotalPrice = view.findViewById<TextView>(R.id.tvTotalPrice)
        tvTotalPrice.text = "235.000đ"

        val tvResultPrice = view.findViewById<TextView>(R.id.tvResultPrice)
        tvResultPrice.text = "235.000đ"

        val tvTotalDiscount = view.findViewById<TextView>(R.id.tvTotalDiscount)
        tvTotalDiscount.text = "35.000đ"

        val btnConfirmPayment = view.findViewById<View>(R.id.btnConfirmPayment)
        btnConfirmPayment.setOnClickListener {
            Log.d("PaymentFragment", "Confirm payment clicked")
        }


    }


}