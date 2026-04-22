package com.example.hcmus_quickhelper.features.payment.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.core.utils.MoneyUtils
import com.example.hcmus_quickhelper.core.utils.toSmartTime
import com.example.hcmus_quickhelper.databinding.FragmentPaymentBinding
import com.example.hcmus_quickhelper.features.payment.datasource.PaymentDataSource
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
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var bookingId: Int = 2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bookingId = arguments?.getInt("booking_id") ?: bookingId

        setupViewModel()
        setupObservers()

        if (viewModel.payment.value == null) {
            viewModel.loadBooking(bookingId)
        }
        
        setupVoucherResultListener()

        binding.layoutVoucherPicker.setOnClickListener { showVoucherPicker() }
        binding.btnBack.setOnClickListener { handleBack() }
        binding.btnConfirmPayment.setOnClickListener {
            val selectedId = binding.radioGroupPaymentMethod.checkedRadioButtonId
            if (selectedId != -1) {
                val radioButton: RadioButton = binding.root.findViewById(selectedId)
                val method = radioButton.text.toString()
                viewModel.submitPayment(method)
                submitPayment()
            } else {
                Toast.makeText(requireContext(), "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupViewModel() {
        val paymentRepository = PaymentRepository(PaymentDataSource())

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return PaymentViewModel(paymentRepository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[PaymentViewModel::class.java]
    }

    private fun setupVoucherResultListener() {
        val navBackStackEntry = findNavController().currentBackStackEntry
        navBackStackEntry?.savedStateHandle?.getLiveData<Voucher>("selected_voucher")
            ?.observe(viewLifecycleOwner) { voucher ->
                voucher?.let {
                    Log.d("PaymentFragment", "Voucher selected: ${it.code}")
                    viewModel.setVoucher(it)
                    navBackStackEntry.savedStateHandle.remove<Voucher>("selected_voucher")
                }
            }
    }

    private fun setupObservers() {
        viewModel.payment.observe(viewLifecycleOwner) { payment ->
            payment?.let {
                if (it.voucher != null) {
                    binding.layoutVoucher.visibility = View.VISIBLE
                    binding.tvCodeVoucher.text = it.voucher?.code
                    binding.tvVoucherItemDiscount.text = "giảm ${MoneyUtils.formatVietnameseCurrency(it.voucher?.discount ?: 0.0)}"
                    binding.tvVoucherDiscount.text = "-${MoneyUtils.formatVietnameseCurrency(it.voucher?.discount ?: 0.0)}"
                    binding.btnCancelVoucher.setOnClickListener {
                        viewModel.setVoucher(null)
                    }
                } else {
                    binding.layoutVoucher.visibility = View.GONE
                    binding.tvVoucherDiscount.text = "0đ"
                }

                it.booking?.let { booking ->
                    binding.tvAddress.text = booking.address
                    binding.tvDateBooking.text = booking.schedule.toSmartTime()
                    binding.tvServicePrice.text = MoneyUtils.formatVietnameseCurrency(booking.totalPrice)

                    booking.service?.let { service ->
                        binding.tvServiceName.text = service.name
                    }

                    booking.helper?.let { helper ->
                        binding.tvHelperName.text = helper.fullname
                        binding.tvHelperRating.text = helper.rating.toString()
                    }
                }


                binding.tvTotalPrice.text = MoneyUtils.formatVietnameseCurrency(it.amount)
            }
        }
    }

    private fun showVoucherPicker() {
        findNavController().navigate(R.id.action_payment_fragment_to_voucher_fragment)
    }

    private fun handleBack() {
        findNavController().popBackStack()
    }

    private fun submitPayment() {
        val bundle = Bundle().apply {
            putInt("payment_id", viewModel.payment.value?.id!!)
        }
        findNavController().navigate(R.id.action_payment_fragment_to_receipt_fragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
