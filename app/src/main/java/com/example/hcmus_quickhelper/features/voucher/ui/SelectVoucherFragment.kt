package com.example.hcmus_quickhelper.features.voucher.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.core.utils.MoneyUtils
import com.example.hcmus_quickhelper.databinding.FragmentSelectVoucherBinding
import com.example.hcmus_quickhelper.features.voucher.datasource.VoucherDataSource
import com.example.hcmus_quickhelper.features.voucher.model.Voucher
import com.example.hcmus_quickhelper.features.voucher.repository.VoucherRepository
import com.example.hcmus_quickhelper.features.voucher.viewmodel.SelectVoucherViewModel

class SelectVoucherFragment : Fragment(R.layout.fragment_select_voucher) {

    private lateinit var viewModel: SelectVoucherViewModel
    private lateinit var adapter: SelectVoucherAdapter

    private var _binding: FragmentSelectVoucherBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectVoucherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupUI()
        observeData()

        binding.btnBack.setOnClickListener { handleBack() }
        binding.btnSubmit.setOnClickListener { handleSubmit(viewModel.voucher.value) }
    }

    private fun setupViewModel() {
        val dataSource = VoucherDataSource()
        val repository = VoucherRepository(dataSource)

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return SelectVoucherViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[SelectVoucherViewModel::class.java]
    }

    private fun setupUI() {
        adapter = SelectVoucherAdapter(vouchers = emptyList()) { voucher ->
            viewModel.setVoucher(voucher)
        }

        binding.rvVoucher.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@SelectVoucherFragment.adapter
            setHasFixedSize(true)
        }
    }

    private fun observeData() {
        viewModel.vouchers.observe(viewLifecycleOwner) { list ->
            list?.let {
                adapter.updateData(it)
                binding.tvQuantity.text = "Tất cả (${it.size})"
            }
        }

        viewModel.voucher.observe(viewLifecycleOwner) {voucher ->
            adapter.updateSelectedVoucher(voucher.id)
            binding.tvDiscount.text = MoneyUtils.formatVietnameseCurrency(voucher.discount)
        }

        viewModel.loadVouchers()
    }

    private fun handleBack() {
        findNavController().popBackStack()
    }

    private fun handleSubmit(selectedVoucher: Voucher?) {
        val savedStateHandle = findNavController().previousBackStackEntry?.savedStateHandle

        savedStateHandle?.set("selected_voucher", selectedVoucher)
        findNavController().popBackStack()
    }
}