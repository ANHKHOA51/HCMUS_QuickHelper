package com.example.hcmus_quickhelper.features.voucher.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.databinding.FragmentAddVoucherBinding
import com.example.hcmus_quickhelper.databinding.FragmentCollectVoucherBinding
import com.example.hcmus_quickhelper.features.voucher.datasource.VoucherDataSource
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddVoucherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()

        binding.btnSave.setOnClickListener {
            val code = binding.etCode.text.toString().trim()
            val quantityString = binding.etQuantity.text.toString().trim()
            val discountString = binding.etDiscount.text.toString().trim()
            val minPriceString = binding.etMinPrice.text.toString().trim()
            val expiredAt = binding.btnDate.text.toString().trim() + " " + binding.btnTime.text.toString().trim()



            // 2. Kiểm tra dữ liệu (Validation) - Dựa trên ràng buộc NOT NULL của DB
            if (code.isEmpty()) {
                binding.etCode.error = "Mã voucher không được để trống"
                return@setOnClickListener
            }

            if (discountString.isEmpty()) {
                binding.etDiscount.error = "Vui lòng nhập giá trị giảm"
                return@setOnClickListener
            }

            // 3. Chuyển đổi kiểu dữ liệu phù hợp với Database
            // quantity mặc định là 0 nếu để trống (theo SQL: default 0)
            val quantity = quantityString.toIntOrNull() ?: 0

            // discount và minPrice là numeric (thường dùng Double hoặc BigDecimal)
            val discount = discountString.toDoubleOrNull() ?: 0.0
            val minPrice = minPriceString.toDoubleOrNull() ?: 0.0

            // 4. Log dữ liệu ra Logcat để kiểm tra
            val logMessage = """
                --- DỮ LIỆU VOUCHER MỚI ---
                Mã: $code
                Số lượng: $quantity
                Giảm giá: $discount VNĐ
                Đơn tối thiểu: $minPrice VNĐ
                Hết hạn: $expiredAt
                ---------------------------
            """.trimIndent()

            android.util.Log.d("VoucherData", logMessage)

            // Thông báo cho người dùng (tùy chọn)
            android.widget.Toast.makeText(requireContext(), "Đã ghi nhận dữ liệu Voucher!", android.widget.Toast.LENGTH_SHORT).show()
        }

        setupListeners()
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
    }

}