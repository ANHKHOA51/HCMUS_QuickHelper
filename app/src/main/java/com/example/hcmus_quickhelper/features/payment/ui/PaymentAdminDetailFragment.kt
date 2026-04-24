package com.example.hcmus_quickhelper.features.payment.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.hcmus_quickhelper.core.utils.MoneyUtils
import com.example.hcmus_quickhelper.core.utils.toSmartTime
import com.example.hcmus_quickhelper.databinding.FragmentPaymentAdminDetailBinding
import com.example.hcmus_quickhelper.features.payment.datasource.PaymentDataSource
import com.example.hcmus_quickhelper.features.payment.model.PaymentMethod
import com.example.hcmus_quickhelper.features.payment.model.PaymentStatus
import com.example.hcmus_quickhelper.features.payment.repository.PaymentRepository
import com.example.hcmus_quickhelper.features.payment.viewmodel.PaymentAdminDetailViewModel
import com.example.hcmus_quickhelper.features.payment.viewmodel.PaymentAdminViewModel

class PaymentAdminDetailFragment : Fragment() {

    private var _binding: FragmentPaymentAdminDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PaymentAdminDetailViewModel
    private var paymentId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentAdminDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        paymentId = arguments?.getInt("paymentId") ?: -1

        setupViewModel()
        setupObserve()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        if (paymentId != -1) {
            viewModel.loadPayment(paymentId)
        }
    }

    private fun setupViewModel() {
        val repository = PaymentRepository(PaymentDataSource())
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return PaymentAdminDetailViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[PaymentAdminDetailViewModel::class.java]
    }

    private fun setupObserve() {
        viewModel.payment.observe(viewLifecycleOwner) { payment ->
            payment?.let { displayPaymentDetail(it) }
        }
    }

    private fun displayPaymentDetail(payment: com.example.hcmus_quickhelper.features.payment.model.Payment) {
        binding.apply {
            // 1. Trạng thái & Chung
            tvPaymentStatus.text = when (payment.status) {
                PaymentStatus.SUCCESS.toString() -> "THÀNH CÔNG"
                PaymentStatus.PENDING.toString() -> "CHỜ XỬ LÝ"
                else -> payment.status.uppercase()
            }
            tvPaymentId.text = "Mã giao dịch: #${payment.id}"
            tvAmount.text = MoneyUtils.formatVietnameseCurrency(payment.amount)

            // 2. Thông tin liên quan
            tvCustomerName.text = "Khách hàng: ${payment.booking?.customer?.fullname ?: "N/A"}"
            tvHelperName.text = "Người giúp việc: ${payment.booking?.helper?.fullname ?: "Chưa có"}"
            tvPaymentMethod.text = "Phương thức thanh toán: ${PaymentMethod.fromString(payment.method).displayName}"

            // 3. Chi tiết dịch vụ
            tvServiceName.text = payment.booking?.service?.name ?: "Dịch vụ"
            tvBookingDate.text = "Thời gian thực hiện: ${payment.booking?.schedule?.toSmartTime() ?: "N/A"}"
            tvAddress.text = "Địa chỉ: ${payment.booking?.address ?: "N/A"}"

            // 4. Chi tiết thanh toán
            val servicePrice = payment.booking?.totalPrice ?: 0.0
            val discount = payment.voucher?.discount ?: 0.0

            tvServicePrice.text = MoneyUtils.formatVietnameseCurrency(servicePrice)
            tvVoucherDiscount.text = "-${MoneyUtils.formatVietnameseCurrency(discount)}"
            tvTotalPrice.text = MoneyUtils.formatVietnameseCurrency(payment.amount)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
