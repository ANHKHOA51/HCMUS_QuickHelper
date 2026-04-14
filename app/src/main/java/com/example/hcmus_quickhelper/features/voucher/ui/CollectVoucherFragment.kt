package com.example.hcmus_quickhelper.features.voucher.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.databinding.FragmentCollectVoucherBinding
import com.example.hcmus_quickhelper.databinding.FragmentSelectVoucherBinding
import com.example.hcmus_quickhelper.features.voucher.datasource.VoucherDataSource
import com.example.hcmus_quickhelper.features.voucher.model.Voucher
import com.example.hcmus_quickhelper.features.voucher.repository.VoucherRepository
import com.example.hcmus_quickhelper.features.voucher.viewmodel.CollectVoucherViewModel
import com.example.hcmus_quickhelper.features.voucher.viewmodel.SelectVoucherViewModel

class CollectVoucherFragment : Fragment() {
    private lateinit var viewModel: CollectVoucherViewModel
    private lateinit var adapter: CollectVoucherAdapter
    private var _binding: FragmentCollectVoucherBinding? = null
    private val binding get() = _binding!!

    private var userId: Int = 2 // MOCK

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollectVoucherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupUI()
        setupObserve()

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
    }

    private  fun setupViewModel() {
        val repository = VoucherRepository(VoucherDataSource())

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return CollectVoucherViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[CollectVoucherViewModel::class.java]
    }

    private fun setupUI() {
        adapter = CollectVoucherAdapter(vouchers = emptyList()) { voucher ->
            Log.d("TEST", voucher.toString())
        }

        binding.rvVoucher.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@CollectVoucherFragment.adapter
            setHasFixedSize(true)
        }
    }

    private fun setupObserve() {
        viewModel.vouchers.observe(viewLifecycleOwner) { list ->
            list?.let {
                adapter.updateData(it)
            }
        }

        viewModel.loadVouchers(userId)
    }
}