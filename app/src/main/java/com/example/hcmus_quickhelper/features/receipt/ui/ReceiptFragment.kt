package com.example.hcmus_quickhelper.features.receipt.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.BundleCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.core.model.Booking
import com.example.hcmus_quickhelper.core.utils.MoneyUtils
import com.example.hcmus_quickhelper.core.utils.toSmartTime
import com.example.hcmus_quickhelper.databinding.FragmentPaymentBinding
import com.example.hcmus_quickhelper.databinding.FragmentReceiptBinding
import com.example.hcmus_quickhelper.features.payment.model.Payment
import com.example.hcmus_quickhelper.features.payment.viewmodel.PaymentViewModel
import com.example.hcmus_quickhelper.features.receipt.viewmodel.ReceiptViewModel
import com.example.hcmus_quickhelper.features.voucher.model.Voucher

class ReceiptFragment : Fragment(R.layout.fragment_receipt) {

    private val viewModel: ReceiptViewModel by viewModels()

    private var _binding: FragmentReceiptBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReceiptBinding.inflate(inflater, container, false)

        val payment = arguments?.let {
            BundleCompat.getParcelable(it, "payment_info", Payment::class.java)
        }

        val booking = arguments?.let {
            BundleCompat.getParcelable(it, "booking_info", Booking::class.java)
        }

        val voucher = arguments?.let {
            BundleCompat.getParcelable(it, "voucher_info", Voucher::class.java)
        }

        if(payment != null) viewModel.setPayment(payment)
        if(booking != null) viewModel.setBooking(booking)
        if(voucher != null) viewModel.setVoucher(voucher)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        setupViewModel()
        setupObservers()
    }

//    private fun setupViewModel() {
//        viewModel = ViewModelProvider(this)[ReceiptViewModel::class.java]
//    }

    private fun setupObservers() {
        viewModel.payment.observe(viewLifecycleOwner) { payment ->
            payment?.let {
                binding.tvPaymentId.text = "Mã hóa đơn: #${it.id}"
            }
        }

        viewModel.voucher.observe(viewLifecycleOwner) {voucher ->
            voucher?.let{
                binding.tvVoucherDiscount.text = "-${MoneyUtils.formatVietnameseCurrency(it.discount)}"
            }
        }

        viewModel.booking.observe(viewLifecycleOwner) {booking ->
            booking?.let{
                binding.tvDate.text = it.schedule.toSmartTime()
                binding.tvServicePrice.text = "${MoneyUtils.formatVietnameseCurrency(it.totalPrice)}"

                binding.tvTotalPrice.text = "${MoneyUtils.formatVietnameseCurrency(viewModel.calcTotalPrice())}"
            }
        }
    }
}