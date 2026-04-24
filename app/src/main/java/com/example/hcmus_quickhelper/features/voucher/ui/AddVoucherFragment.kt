package com.example.hcmus_quickhelper.features.voucher.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import coil.util.Logger
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.databinding.FragmentAddVoucherBinding
import com.example.hcmus_quickhelper.databinding.FragmentCollectVoucherBinding
import com.example.hcmus_quickhelper.features.voucher.datasource.VoucherDataSource
import com.example.hcmus_quickhelper.features.voucher.model.VoucherInsert
import com.example.hcmus_quickhelper.features.voucher.repository.VoucherRepository
import com.example.hcmus_quickhelper.features.voucher.viewmodel.AddVoucherViewModel
import com.example.hcmus_quickhelper.features.voucher.viewmodel.CollectVoucherViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class AddVoucherFragment : Fragment() {
    private lateinit var viewModel: AddVoucherViewModel

    private var _binding: FragmentAddVoucherBinding? = null
    private val binding get() = _binding!!

    private val calendar = Calendar.getInstance()

    private var voucherId = -1;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddVoucherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        voucherId = arguments?.getInt("voucherId") ?: -1

        setupViewModel()
        setupObserver()
        setupListeners()

        if(voucherId != -1) {
            viewModel.loadVoucher(voucherId)
        }
    }

    private fun setupViewModel() {
        val repository = VoucherRepository(VoucherDataSource())

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AddVoucherViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[AddVoucherViewModel::class.java]
    }

    private fun setupObserver() {
        viewModel.voucher.observe(viewLifecycleOwner) { voucher ->
            voucher?.let {
                binding.apply {
                    // Đổ dữ liệu vào EditText
                    etCode.setText(it.code)
                    etQuantity.setText(it.quantity.toString())
                    etDiscount.setText(it.discount.toString())
                    etMinPrice.setText(it.minPrice.toString())

                    tvHeaderTitle.text = "Chỉnh sửa Voucher"
                    btnSave.text = "Cập nhật Voucher"

                    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    val date = format.parse(it.expiredAt)

                    date?.let { d ->
                        calendar.time = d

                        val dateShow = SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                        ).format(calendar.time)
                        val timeShow =
                            SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)

                        binding.btnDate.text = dateShow
                        binding.btnTime.text = timeShow
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    binding.btnDate.text = dateFormat.format(calendar.time)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.btnTime.setOnClickListener {
            TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                    binding.btnTime.text = timeFormat.format(calendar.time)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true // Dùng 24h format
            ).show()
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSave.setOnClickListener {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val code = binding.etCode.text.toString().trim()
            val quantityString = binding.etQuantity.text.toString().trim()
            val discountString = binding.etDiscount.text.toString().trim()
            val minPriceString = binding.etMinPrice.text.toString().trim()
            val expiredAt = format.format(calendar.time)

            if (code.isEmpty()) {
                binding.etCode.error = "Mã voucher không được để trống"
                return@setOnClickListener
            }

            if (discountString.isEmpty()) {
                binding.etDiscount.error = "Vui lòng nhập giá trị giảm"
                return@setOnClickListener
            }

            val quantity = quantityString.toIntOrNull() ?: 0

            val discount = discountString.toDoubleOrNull() ?: 0.0
            val minPrice = minPriceString.toDoubleOrNull() ?: 0.0

            if(voucherId != -1) {
                viewModel.updateVoucher(voucherId, VoucherInsert(
                    code = code,
                    quantity = quantity,
                    discount = discount,
                    minPrice = minPrice,
                    expiredAt = expiredAt
                ))
            } else {
                viewModel.addVoucher(VoucherInsert(
                    code = code,
                    quantity = quantity,
                    discount = discount,
                    minPrice = minPrice,
                    expiredAt = expiredAt
                ))
            }

            findNavController().popBackStack()
        }
    }

}