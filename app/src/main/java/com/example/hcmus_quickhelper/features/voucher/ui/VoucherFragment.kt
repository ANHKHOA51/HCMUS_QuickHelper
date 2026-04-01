package com.example.hcmus_quickhelper.features.voucher.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.databinding.FragmentVoucherBinding
import com.example.hcmus_quickhelper.features.voucher.datasource.MockVoucherDataSource
import com.example.hcmus_quickhelper.features.voucher.model.Voucher
import com.example.hcmus_quickhelper.features.voucher.repository.VoucherRepository
import com.example.hcmus_quickhelper.features.voucher.viewmodel.VoucherViewModel

class VoucherFragment : Fragment(R.layout.fragment_voucher) {

    private lateinit var viewModel: VoucherViewModel
    private lateinit var adapter: VoucherAdapter

    private var _binding: FragmentVoucherBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVoucherBinding.inflate(inflater, container, false)
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
        val dataSource = MockVoucherDataSource()
        val repository = VoucherRepository(dataSource)

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return VoucherViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[VoucherViewModel::class.java]
    }

    private fun setupUI() {
        adapter = VoucherAdapter(vouchers = emptyList()) { voucher ->
            viewModel.setVoucher(voucher)
        }

        binding.rvVoucher.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@VoucherFragment.adapter
            setHasFixedSize(true)
        }
    }

    private fun observeData() {
        viewModel.vouchers.observe(viewLifecycleOwner) { list ->
            list?.let {
                adapter.updateData(it)
            }
        }

        viewModel.voucher.observe(viewLifecycleOwner) {voucher ->
            adapter.updateSelectedVoucher(voucher.id)
        }

        viewModel.loadVouchers()
    }

    private fun handleBack() {
        Log.d("DEBUG", "Back button clicked")
        if (parentFragmentManager.backStackEntryCount > 0) {
            parentFragmentManager.popBackStack()
        } else {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
    }

    private fun handleSubmit(selectedVoucher: Voucher?) {
        val result = Bundle().apply {
            putParcelable("SELECTED_VOUCHER", selectedVoucher)
        }

        parentFragmentManager.setFragmentResult("VOUCHER_SELECTION", result)

        parentFragmentManager.popBackStack()
    }
}