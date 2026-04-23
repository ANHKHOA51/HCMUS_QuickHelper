package com.example.hcmus_quickhelper.features.voucher.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.databinding.FragmentVoucherManagementBinding
import com.example.hcmus_quickhelper.features.voucher.datasource.VoucherDataSource
import com.example.hcmus_quickhelper.features.voucher.model.Voucher
import com.example.hcmus_quickhelper.features.voucher.repository.VoucherRepository
import com.example.hcmus_quickhelper.features.voucher.viewmodel.VoucherManagementViewModel

class VoucherManagementFragment : Fragment() {

    private var _binding: FragmentVoucherManagementBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: VoucherManagementViewModel
    private val voucherAdapter by lazy {
        VoucherAdminAdapter(
            onEditClick = { voucher -> goToEditVoucher(voucher) },
            onDeleteClick = { voucher -> showDeleteConfirmDialog(voucher) }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVoucherManagementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        setupObserve()

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnAdd.setOnClickListener { goToAddVoucher() }

        viewModel.loadVouchers()
    }

    private fun setupViewModel() {
        val repository = VoucherRepository(VoucherDataSource())
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return VoucherManagementViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[VoucherManagementViewModel::class.java]
    }

    private fun setupRecyclerView() {
        binding.rvVouchers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = voucherAdapter
        }
    }

    private fun setupObserve() {
        viewModel.vouchers.observe(viewLifecycleOwner) { list ->
            voucherAdapter.updateData(list)
        }

    }

    private fun goToAddVoucher() {
//        findNavController().navigate(R.id.action_voucher_management_to_add_edit_voucher)
    }

    private fun goToEditVoucher(voucher: Voucher) {
        val bundle = Bundle().apply {
            putParcelable("voucher", voucher)
        }
//        findNavController().navigate(R.id.action_voucher_management_to_add_edit_voucher, bundle)
    }

    private fun showDeleteConfirmDialog(voucher: Voucher) {
        AlertDialog.Builder(requireContext())
            .setTitle("Xóa Voucher")
            .setMessage("Bạn có chắc chắn muốn xóa mã ${voucher.code} không?")
            .setPositiveButton("Xóa") { _, _ ->
                viewModel.deleteVoucher(voucher.id)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
