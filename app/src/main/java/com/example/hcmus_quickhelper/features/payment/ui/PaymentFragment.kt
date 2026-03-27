package com.example.hcmus_quickhelper.features.payment.ui

import android.icu.text.NumberFormat
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.features.payment.datasource.MockPaymentDataSource
import com.example.hcmus_quickhelper.features.payment.model.Payment
import com.example.hcmus_quickhelper.features.payment.repository.PaymentRepository
import com.example.hcmus_quickhelper.features.payment.viewmodel.PaymentViewModel
import com.example.hcmus_quickhelper.features.voucher.datasource.MockVoucherDataSource
import com.example.hcmus_quickhelper.features.voucher.repository.VoucherRepository
import com.example.hcmus_quickhelper.features.voucher.viewmodel.VoucherViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeFormat
import java.time.format.DateTimeFormatter
import java.util.Locale


class PaymentFragment : Fragment(R.layout.fragment_payment) {
    private lateinit var viewModel: PaymentViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupObservers(view)
    }

    private fun setupViewModel() {
        val dataSource = MockPaymentDataSource()
        val repository = PaymentRepository(dataSource)

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return PaymentViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[PaymentViewModel::class.java]
    }

    private fun setupObservers(view: View) {
        // Collect StateFlow safely with the lifecycle
        viewModel.payment.observe(viewLifecycleOwner) {payment ->
            if(payment != null) {
                updateUI(view, payment)
            }
        }
    }

    private fun updateUI(view: View, payment: Payment) {
        // 1. Thông tin dịch vụ và người giúp việc
        view.findViewById<TextView>(R.id.tvServiceName).text = payment.service
        view.findViewById<TextView>(R.id.tvHelperName).text = payment.helper
        view.findViewById<TextView>(R.id.tvAddressBooking).text = payment.address

        // 2. Xử lý Ngày và Giờ (Sử dụng ISO_LOCAL_DATE_TIME hoặc format tùy chỉnh)
        try {
//            val datetime = LocalDateTime.parse(payment.createdAt)
//            val dateFormatter: DateTimeFormatter? = DateTimeFormatter.ofPattern("dd/MM/yyyy")
//            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
//
//            view.findViewById<TextView>(R.id.tvDateBooking).text = datetime.format(dateFormatter)
//            view.findViewById<TextView>(R.id.tvTimeBooking).text = datetime.format(timeFormatter)
        } catch (e: Exception) {
            Log.e("PaymentFragment", "Lỗi định dạng ngày: ${payment.createdAt}", e)
            view.findViewById<TextView>(R.id.tvDateBooking).text = payment.createdAt
        }

        // 3. Định dạng Tiền tệ (VND)
        val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        val formattedAmount = currencyFormatter.format(payment.amount.toLong())

        // Gán vào các TextView liên quan đến giá
        view.findViewById<TextView>(R.id.tvServicePrice).text = formattedAmount
        view.findViewById<TextView>(R.id.tvTotalPrice).text = formattedAmount
        view.findViewById<TextView>(R.id.tvResultPrice).text = formattedAmount
    }
}