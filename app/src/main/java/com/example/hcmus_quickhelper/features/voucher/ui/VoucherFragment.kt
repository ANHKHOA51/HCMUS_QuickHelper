package com.example.hcmus_quickhelper.features.voucher.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.features.voucher.datasource.MockVoucherDataSource
import com.example.hcmus_quickhelper.features.voucher.model.Voucher
import com.example.hcmus_quickhelper.features.voucher.repository.VoucherRepository
import com.example.hcmus_quickhelper.features.voucher.viewmodel.VoucherViewModel

class VoucherFragment : Fragment(R.layout.fragment_voucher) { // Truyền layout trực tiếp vào constructor

    private lateinit var viewModel: VoucherViewModel
    private lateinit var adapter: VoucherAdapter
    private lateinit var rvVoucher: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupUI(view)
        observeData()
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

    private fun setupUI(view: View) {
        rvVoucher = view.findViewById(R.id.rvVoucher)

        // Khởi tạo adapter
        adapter = VoucherAdapter{voucher ->
            println("Clicked on: ${voucher.code}")
        }

        // Cấu hình RecyclerView
        rvVoucher.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@VoucherFragment.adapter
            setHasFixedSize(true) // Tối ưu hiệu năng nếu kích thước item cố định
        }
    }

    private fun observeData() {
        viewModel.vouchers.observe(viewLifecycleOwner) { list ->
            list?.let {
                adapter.updateData(it)
            }
        }

        viewModel.loadVouchers()
    }
}