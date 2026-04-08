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
import androidx.navigation.fragment.findNavController
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.core.utils.MoneyUtils
import com.example.hcmus_quickhelper.databinding.FragmentPaymentBinding
import com.example.hcmus_quickhelper.databinding.FragmentRatingBinding
import com.example.hcmus_quickhelper.features.payment.datasource.MockPaymentDataSource
import com.example.hcmus_quickhelper.features.payment.datasource.PaymentDataSource
import com.example.hcmus_quickhelper.features.payment.model.Payment
import com.example.hcmus_quickhelper.features.payment.repository.PaymentRepository
import com.example.hcmus_quickhelper.features.payment.viewmodel.PaymentViewModel
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
        val navBackStackEntry = findNavController().currentBackStackEntry
        navBackStackEntry?.savedStateHandle?.getLiveData<Voucher>("selected_voucher")
            ?.observe(viewLifecycleOwner) { voucher ->
                voucher?.let {
                    viewModel.setVoucher(it)
                    navBackStackEntry.savedStateHandle.remove<Voucher>("selected_voucher")
                }
            }

        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var paymentId: Int = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupObservers()
        updateUI()

        binding.layoutVoucherPicker.setOnClickListener { showVoucherPicker() }
        binding.btnBack.setOnClickListener { handleBack() }
    }

    private fun setupViewModel() {
        val dataSource = PaymentDataSource()
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
        viewModel.payment.observe(viewLifecycleOwner) { payment ->
            payment?.let {

            }
        }

        viewModel.voucher.observe(viewLifecycleOwner) {voucher ->
            if (voucher != null) {
                binding.layoutVoucher.visibility = View.VISIBLE

                binding.tvCodeVoucher.text = voucher.code
                binding.tvVoucherItemDiscount.text = "giảm ${MoneyUtils.formatVietnameseCurrency(voucher.discount)}"

                binding.btnCancelVoucher.setOnClickListener {
                    viewModel.setVoucher(null)
                }
            } else {
                binding.layoutVoucher.visibility = View.GONE
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isVisible ->
            // loading
        }
    }

    private fun updateUI() {
        viewModel.loadPayment(paymentId)
    }

    private fun showVoucherPicker() {
        findNavController().navigate(R.id.action_payment_fragment_to_voucher_fragment)
    }

    private fun handleBack() {
        Log.d("DEBUG", "BACK")
    }
}