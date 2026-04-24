package com.example.hcmus_quickhelper.features.voucher.ui

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.core.auth.SessionManager
import com.example.hcmus_quickhelper.databinding.FragmentCollectVoucherBinding
import com.example.hcmus_quickhelper.features.voucher.datasource.VoucherDataSource
import com.example.hcmus_quickhelper.features.voucher.repository.VoucherRepository
import com.example.hcmus_quickhelper.features.voucher.viewmodel.CollectVoucherTab
import com.example.hcmus_quickhelper.features.voucher.viewmodel.CollectVoucherViewModel

class CollectVoucherFragment : Fragment() {
    private lateinit var viewModel: CollectVoucherViewModel
    private val collectAdapter by lazy {
        CollectVoucherAdapter(vouchers = emptyList()) {voucher ->
            viewModel.collectVoucher(voucher.id, userId)
        }
    }
    private val selectAdapter by lazy {
        SelectVoucherAdapter(vouchers = emptyList()) { voucher ->
            Log.d("TEST", "Selected from storage: ${voucher.code}")
        }
    }

    private var _binding: FragmentCollectVoucherBinding? = null
    private val binding get() = _binding!!

    private var userId: Int = 9 // MOCK

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollectVoucherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = SessionManager.currentUser.value?.id!!;

        setupViewModel()
        setupRecyclerView()
        setupObserve()
        setupTabListeners()

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
    }

    private fun setupViewModel() {
        val repository = VoucherRepository(VoucherDataSource())

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return CollectVoucherViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[CollectVoucherViewModel::class.java]
        viewModel.loadData(userId)
    }

    private fun setupRecyclerView() {
        binding.rvVoucher.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun setupObserve() {
        viewModel.currentTab.observe(viewLifecycleOwner) { tab ->
            updateTabUI(tab)
            if(tab == CollectVoucherTab.COLLECT) {
                binding.rvVoucher.adapter = collectAdapter
                viewModel.loadCollectibleVouchers(userId)
            }
            else {
                binding.rvVoucher.adapter = selectAdapter
                viewModel.loadMyVouchers(userId)
            }
        }

        viewModel.vouchers.observe(viewLifecycleOwner) { list ->
            list?.let {
                val currentAdapter = binding.rvVoucher.adapter
                if (currentAdapter is CollectVoucherAdapter) {
                    currentAdapter.updateData(it)
                } else if (currentAdapter is SelectVoucherAdapter) {
                    currentAdapter.updateData(it)
                }
            }
        }
    }

    private fun setupTabListeners() {
        binding.cvTabCollect.setOnClickListener { viewModel.selectTab(CollectVoucherTab.COLLECT) }
        binding.cvTabStorage.setOnClickListener { viewModel.selectTab(CollectVoucherTab.STORAGE) }
    }

    private fun updateTabUI(tab: CollectVoucherTab) {
        val inactiveBgColor = Color.TRANSPARENT
        val inactiveTextColor = Color.parseColor("#888888")

        val activeBgColor = Color.WHITE
        val activeTextColor = ContextCompat.getColor(
            requireContext(),
            R.color.orange_primary
        )

        val tabs = listOf(
            Pair(binding.cvTabCollect, binding.tvTabCollect),
            Pair(binding.cvTabStorage, binding.tvTabStorage)
        )

        tabs.forEach { (cv, tv) ->
            cv.setCardBackgroundColor(inactiveBgColor)
            cv.cardElevation = 0f
            tv.setTextColor(inactiveTextColor)
            tv.setTypeface(null, Typeface.NORMAL)
        }

        when (tab) {
            CollectVoucherTab.COLLECT -> {
                binding.cvTabCollect.setCardBackgroundColor(activeBgColor)
                binding.cvTabCollect.cardElevation = 4f
                binding.tvTabCollect.setTextColor(activeTextColor)
                binding.tvTabCollect.setTypeface(null, Typeface.BOLD)
            }
            CollectVoucherTab.STORAGE -> {
                binding.cvTabStorage.setCardBackgroundColor(activeBgColor)
                binding.cvTabStorage.cardElevation = 4f
                binding.tvTabStorage.setTextColor(activeTextColor)
                binding.tvTabStorage.setTypeface(null, Typeface.BOLD)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
