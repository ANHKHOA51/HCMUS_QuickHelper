package com.example.hcmus_quickhelper.features.payment.ui

import android.icu.text.NumberFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.databinding.FragmentPaymentBinding
import com.example.hcmus_quickhelper.databinding.FragmentRatingBinding
import com.example.hcmus_quickhelper.features.payment.datasource.MockPaymentDataSource
import com.example.hcmus_quickhelper.features.payment.model.Payment
import com.example.hcmus_quickhelper.features.payment.repository.PaymentRepository
import com.example.hcmus_quickhelper.features.payment.viewmodel.PaymentViewModel
import com.example.hcmus_quickhelper.features.voucher.datasource.MockVoucherDataSource
import com.example.hcmus_quickhelper.features.voucher.model.Voucher
import com.example.hcmus_quickhelper.features.voucher.repository.VoucherRepository
import com.example.hcmus_quickhelper.features.voucher.ui.VoucherFragment
import com.example.hcmus_quickhelper.features.voucher.viewmodel.VoucherViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeFormat
import java.time.format.DateTimeFormatter
import java.util.Locale


class PaymentFragment : Fragment(R.layout.fragment_payment) {
    private lateinit var viewModel: PaymentViewModel

    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupObservers()

        binding.layoutVoucherPicker.setOnClickListener { showVoucherPicker() }
        binding.btnBack.setOnClickListener { handleBack() }

        setFragmentResultListener("VOUCHER_SELECTION") { requestKey, bundle ->
            val voucher = bundle.getParcelable<Voucher>("SELECTED_VOUCHER")

            Log.d("TEST", "$voucher")
        }
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

    private fun setupObservers() {

    }

    private fun updateUI() {

    }

    private fun showVoucherPicker() {
        val voucherFragment = VoucherFragment()

        parentFragmentManager.beginTransaction()
            // Thay thế nội dung hiện tại bằng Fragment mới
            // Lưu ý: Thay 'R.id.fragment_container' bằng ID thực tế của FrameLayout chứa Fragment trong Activity của bạn
            .replace(R.id.nav_host, voucherFragment)
            // Lưu Fragment hiện tại vào lịch sử để khi ấn nút Back trên điện thoại có thể quay lại được
            .addToBackStack(null)
            .commit()
    }

    private fun handleBack() {
        Log.d("DEBUG", "Back button clicked")
        if (parentFragmentManager.backStackEntryCount > 0) {
            parentFragmentManager.popBackStack()
        } else {
            // Nếu không còn Fragment nào, đóng Activity chứa nó
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
    }
}