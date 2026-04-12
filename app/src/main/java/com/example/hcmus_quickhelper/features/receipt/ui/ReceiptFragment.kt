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
import androidx.navigation.fragment.findNavController
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.core.model.Booking
import com.example.hcmus_quickhelper.core.utils.MoneyUtils
import com.example.hcmus_quickhelper.core.utils.toSmartTime
import com.example.hcmus_quickhelper.databinding.FragmentPaymentBinding
import com.example.hcmus_quickhelper.databinding.FragmentReceiptBinding
import com.example.hcmus_quickhelper.features.booking.datasource.BookingDataSource
import com.example.hcmus_quickhelper.features.booking.repository.BookingRepository
import com.example.hcmus_quickhelper.features.payment.datasource.PaymentDataSource
import com.example.hcmus_quickhelper.features.payment.model.Payment
import com.example.hcmus_quickhelper.features.payment.repository.PaymentRepository
import com.example.hcmus_quickhelper.features.payment.viewmodel.PaymentViewModel
import com.example.hcmus_quickhelper.features.receipt.viewmodel.ReceiptViewModel
import com.example.hcmus_quickhelper.features.voucher.datasource.VoucherDataSource
import com.example.hcmus_quickhelper.features.voucher.model.Voucher
import com.example.hcmus_quickhelper.features.voucher.repository.VoucherRepository

class ReceiptFragment : Fragment(R.layout.fragment_receipt) {

    private lateinit var viewModel: ReceiptViewModel

    private var _binding: FragmentReceiptBinding? = null
    private val binding get() = _binding!!

    private var paymentId: Int = -1


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReceiptBinding.inflate(inflater, container, false)

        paymentId = arguments?.getInt("payment_id", -1) ?: -1

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupObservers()
        loadData()

        binding.btnGoToRating.setOnClickListener { handleGoToRating(viewModel.booking.value) }
        binding.btnBack.setOnClickListener { handleBack() }
    }

    private fun setupViewModel() {
        val paymentRepository = PaymentRepository(PaymentDataSource())
        val bookingRepository = BookingRepository(BookingDataSource())
        val voucherRepository = VoucherRepository(VoucherDataSource())

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ReceiptViewModel(paymentRepository, bookingRepository, voucherRepository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[ReceiptViewModel::class.java]
    }

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


    private  fun loadData() {
        viewModel.loadData(paymentId)
    }

    private fun handleGoToRating(booking: Booking?) {
        val navController = findNavController()

        val bundle = Bundle().apply {
            putInt("booking_id", booking?.id ?: -1)
        }

        navController.navigate(R.id.action_receipt_fragment_to_rating_fragment, bundle)
    }

    private fun handleBack() {
        findNavController().popBackStack()
    }
}