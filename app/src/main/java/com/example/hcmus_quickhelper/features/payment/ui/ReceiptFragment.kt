package com.example.hcmus_quickhelper.features.payment.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.core.model.Booking
import com.example.hcmus_quickhelper.core.utils.MoneyUtils
import com.example.hcmus_quickhelper.core.utils.toSmartTime
import com.example.hcmus_quickhelper.databinding.FragmentReceiptBinding
import com.example.hcmus_quickhelper.features.booking.datasource.BookingDataSource
import com.example.hcmus_quickhelper.features.booking.repository.BookingRepository
import com.example.hcmus_quickhelper.features.payment.datasource.PaymentDataSource
import com.example.hcmus_quickhelper.features.payment.repository.PaymentRepository
import com.example.hcmus_quickhelper.features.payment.viewmodel.ReceiptViewModel
import com.example.hcmus_quickhelper.features.voucher.datasource.VoucherDataSource
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

        binding.btnGoToRating.setOnClickListener { handleGoToRating(viewModel.payment.value?.bookingId) }
        binding.btnBack.setOnClickListener { handleBack() }
        binding.btnBackToHome.setOnClickListener { findNavController().navigate(R.id.home_fragment) }
        binding.btnDownload.setOnClickListener { handleDownload() }
    }

    private fun setupViewModel() {
        val paymentRepository = PaymentRepository(PaymentDataSource())

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ReceiptViewModel(paymentRepository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[ReceiptViewModel::class.java]
    }

    private fun setupObservers() {
        viewModel.payment.observe(viewLifecycleOwner) { payment ->
            payment?.let {
                binding.tvPaymentId.text = "Mã hóa đơn: #${it.id}"
                binding.tvVoucherDiscount.text = "-${MoneyUtils.formatVietnameseCurrency(it.voucher?.discount!!)}"

                binding.tvDate.text = it.booking?.schedule?.toSmartTime()
                binding.tvServicePrice.text = "${MoneyUtils.formatVietnameseCurrency(it.booking?.totalPrice!!)}"

                binding.tvTotalPrice.text = "${MoneyUtils.formatVietnameseCurrency(it.amount)}"

                binding.tvServiceName.text = it.booking.service?.name
            }
        }
    }

    private  fun loadData() {
        viewModel.loadData(paymentId)
    }

    private fun handleDownload() {

    }

    private fun handleGoToRating(bookingId: Int?) {
        val navController = findNavController()

        val bundle = Bundle().apply {
            putInt("booking_id", bookingId ?: -1)
        }

        navController.navigate(R.id.action_receipt_fragment_to_rating_fragment, bundle)
    }

    private fun handleBack() {
        findNavController().popBackStack()
    }
}