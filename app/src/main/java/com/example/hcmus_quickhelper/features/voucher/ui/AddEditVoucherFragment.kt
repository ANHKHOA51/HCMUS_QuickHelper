//package com.example.hcmus_quickhelper.features.voucher.ui
//
//import android.app.DatePickerDialog
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import androidx.navigation.fragment.findNavController
//import com.example.hcmus_quickhelper.databinding.FragmentAddEditVoucherBinding
//import com.example.hcmus_quickhelper.features.voucher.datasource.VoucherDataSource
//import com.example.hcmus_quickhelper.features.voucher.model.Voucher
//import com.example.hcmus_quickhelper.features.voucher.model.VoucherInsert
//import com.example.hcmus_quickhelper.features.voucher.repository.VoucherRepository
//import com.example.hcmus_quickhelper.features.voucher.viewmodel.VoucherAdminViewModel
//import java.text.SimpleDateFormat
//import java.util.*
//
//class AddEditVoucherFragment : Fragment() {
//
//    private var _binding: FragmentAddEditVoucherBinding? = null
//    private val binding get() = _binding!!
//
//    private lateinit var viewModel: VoucherAdminViewModel
//    private var existingVoucher: Voucher? = null
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentAddEditVoucherBinding.inflate(inflater, container, false)
//        existingVoucher = arguments?.getParcelable("voucher")
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        setupViewModel()
//        setupUI()
//        setupObserve()
//
//        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
//        binding.btnSave.setOnClickListener { handleSave() }
//        binding.etExpiredAt.setOnClickListener { showDatePicker() }
//    }
//
//    private fun setupViewModel() {
//        val repository = VoucherRepository(VoucherDataSource())
//        val factory = object : ViewModelProvider.Factory {
//            override fun <T : ViewModel> create(modelClass: Class<T>): T {
//                @Suppress("UNCHECKED_CAST")
//                return VoucherAdminViewModel(repository) as T
//            }
//        }
//        viewModel = ViewModelProvider(this, factory)[VoucherAdminViewModel::class.java]
//    }
//
//    private fun setupUI() {
//        existingVoucher?.let {
//            binding.tvHeaderTitle.text = "Sửa Voucher"
//            binding.etCode.setText(it.code)
//            binding.etQuantity.setText(it.quantity.toString())
//            binding.etDiscount.setText(it.discount.toLong().toString())
//            binding.etMinPrice.setText(it.minPrice.toLong().toString())
//            binding.etExpiredAt.setText(it.expiredAt.substringBefore("T"))
//        }
//    }
//
//    private fun setupObserve() {
//        viewModel.statusMessage.observe(viewLifecycleOwner) { msg ->
//            msg?.let {
//                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
//                if (it.contains("thành công")) {
//                    findNavController().popBackStack()
//                }
//            }
//        }
//    }
//
//    private fun handleSave() {
//        val code = binding.etCode.text.toString().trim()
//        val quantity = binding.etQuantity.text.toString().toIntOrNull() ?: 0
//        val discount = binding.etDiscount.text.toString().toDoubleOrNull() ?: 0.0
//        val minPrice = binding.etMinPrice.text.toString().toDoubleOrNull() ?: 0.0
//        val expiredAt = binding.etExpiredAt.text.toString()
//
//        if (code.isEmpty() || expiredAt.isEmpty()) {
//            Toast.makeText(requireContext(), "Vui lòng điền mã và ngày hết hạn", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val voucherInsert = VoucherInsert(code, quantity, discount, minPrice, expiredAt)
//
//        if (existingVoucher == null) {
//            viewModel.addVoucher(voucherInsert)
//        } else {
//            viewModel.updateVoucher(existingVoucher!!.id, voucherInsert)
//        }
//    }
//
//    private fun showDatePicker() {
//        val calendar = Calendar.getInstance()
//        DatePickerDialog(
//            requireContext(),
//            { _, year, month, dayOfMonth ->
//                val selectedDate = Calendar.getInstance()
//                selectedDate.set(year, month, dayOfMonth)
//                val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//                binding.etExpiredAt.setText(format.format(selectedDate.time))
//            },
//            calendar.get(Calendar.YEAR),
//            calendar.get(Calendar.MONTH),
//            calendar.get(Calendar.DAY_OF_MONTH)
//        ).show()
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}
