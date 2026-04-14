package com.example.hcmus_quickhelper.features.payment.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.core.model.Booking
import com.example.hcmus_quickhelper.core.utils.MoneyUtils
import com.example.hcmus_quickhelper.core.utils.toSmartTime
import com.example.hcmus_quickhelper.databinding.FragmentPaymentBinding
import com.example.hcmus_quickhelper.features.booking.datasource.BookingDataSource
import com.example.hcmus_quickhelper.features.booking.repository.BookingRepository
import com.example.hcmus_quickhelper.features.payment.datasource.PaymentDataSource
import com.example.hcmus_quickhelper.features.payment.model.Payment
import com.example.hcmus_quickhelper.features.payment.model.PaymentMethod
import com.example.hcmus_quickhelper.features.payment.repository.PaymentRepository
import com.example.hcmus_quickhelper.features.payment.viewmodel.PaymentViewModel
import com.example.hcmus_quickhelper.features.voucher.model.Voucher


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

    private var bookingId: Int = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupObservers()
        updateUI()

        binding.layoutVoucherPicker.setOnClickListener { showVoucherPicker() }
        binding.btnBack.setOnClickListener { handleBack() }
        binding.btnConfirmPayment.setOnClickListener{ submitPayment(viewModel.payment.value, viewModel.booking.value, viewModel.voucher.value) }
    }

    private fun setupViewModel() {
        val paymentRepository = PaymentRepository(PaymentDataSource())
        val bookingRepository = BookingRepository(BookingDataSource())


        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return PaymentViewModel(paymentRepository, bookingRepository) as T
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
                viewModel.payment

                binding.layoutVoucher.visibility = View.VISIBLE

                binding.tvCodeVoucher.text = voucher.code
                binding.tvVoucherItemDiscount.text = "giảm ${MoneyUtils.formatVietnameseCurrency(voucher.discount)}"


                binding.tvVoucherDiscount.text = "-${MoneyUtils.formatVietnameseCurrency(voucher.discount)}"

                binding.btnCancelVoucher.setOnClickListener {
                    viewModel.setVoucher(null)
                }
            } else {
                binding.layoutVoucher.visibility = View.GONE
                binding.tvVoucherDiscount.text = "${MoneyUtils.formatVietnameseCurrency(0.0)}"
            }

            binding.tvTotalPrice.text = MoneyUtils.formatVietnameseCurrency(viewModel.calcTotalPrice())
        }

        viewModel.booking.observe(viewLifecycleOwner) {booking ->
            booking?.let {
                binding.tvAddress.text = it.address
                binding.tvDateBooking.text = it.schedule.toSmartTime()

                binding.tvServicePrice.text = MoneyUtils.formatVietnameseCurrency(it.totalPrice)

                binding.tvTotalPrice.text = MoneyUtils.formatVietnameseCurrency(viewModel.calcTotalPrice())
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isVisible ->
            // loading
        }
    }

    private fun updateUI() {
        viewModel.loadBooking(bookingId)
    }

    private fun showVoucherPicker() {
        findNavController().navigate(R.id.action_payment_fragment_to_voucher_fragment)
    }

    private fun handleBack() {
        Log.d("DEBUG", "BACK")
    }

    private fun submitPayment(payment: Payment?, booking: Booking?, voucher: Voucher?) {

        val selectedId = binding.radioGroupPaymentMethod.checkedRadioButtonId
        if (selectedId != -1) {
            val radioButton: RadioButton = binding.root.findViewById(selectedId)
            val method = PaymentMethod.fromString(radioButton.text.toString()).toString()

            viewModel.savePayment(method)
        }

        val navController = findNavController()

        val bundle = Bundle().apply {
            putInt("payment_id", payment?.id ?: -1)
        }

        navController.navigate(R.id.action_payment_fragment_to_receipt_fragment, bundle)
    }

}