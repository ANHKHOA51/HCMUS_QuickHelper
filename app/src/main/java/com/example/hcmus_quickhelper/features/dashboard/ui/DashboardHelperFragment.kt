package com.example.hcmus_quickhelper.features.dashboard.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.core.model.BookingStatus
import com.example.hcmus_quickhelper.core.utils.MoneyUtils
import com.example.hcmus_quickhelper.databinding.FragmentDashboardHelperBinding
import com.example.hcmus_quickhelper.features.booking.ui.BookingRequestAdapter
import com.example.hcmus_quickhelper.features.dashboard.datasource.DashboardHelperDataSource
import com.example.hcmus_quickhelper.features.dashboard.model.DashboardHelper
import com.example.hcmus_quickhelper.features.dashboard.repository.DashboardHelperRepository
import com.example.hcmus_quickhelper.features.dashboard.viewmodel.DashboardHelperViewModel

class DashboardHelperFragment : Fragment() {
    private var _binding: FragmentDashboardHelperBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DashboardHelperViewModel

    private val bookingAdapter by lazy {
        BookingRequestAdapter() {booking ->
            val bundle = Bundle().apply {
                putInt("bookingId", booking.id)
            }
            if(booking.status == BookingStatus.PENDING.toString() || booking.status == BookingStatus.REJECTED.toString()) {
                findNavController().navigate(
                    R.id.action_dashboard_helper_fragment_to_booking_process_helper_fragment,
                    bundle
                )
            } else {
                findNavController().navigate(
                    R.id.action_dashboard_helper_fragment_to_booking_process_helper_fragment,
                    bundle
                )
            }
        }
    }

    private val helperId = 5 // MOCK

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardHelperBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupRecyclerView()
        setupObserver()

        viewModel.loadData(helperId)
    }

    private fun setupViewModel() {
        val repository = DashboardHelperRepository(DashboardHelperDataSource())
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return DashboardHelperViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[DashboardHelperViewModel::class.java]
    }

    private fun setupRecyclerView() {
        binding.rvRecentActivities.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = bookingAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupObserver() {
        viewModel.dataHelper.observe(viewLifecycleOwner) { data ->
            data?.let {
                updateUI(it)
            }
        }

        viewModel.filterBooking.observe(viewLifecycleOwner) {
            binding.tvRequestCount.text = it.size.toString()
            bookingAdapter.updateData(it)
        }

        viewModel.totalIncome.observe(viewLifecycleOwner) {
            binding.tvTotalIncome.text = MoneyUtils.formatVietnameseCurrency(it)
        }
    }

    private fun updateUI(data: DashboardHelper) {
        binding.apply {
            tvRatingCount.text = data.rating.toString()

            tvSeeAll.setOnClickListener {
                findNavController().navigate(R.id.action_dashboard_helper_fragment_to_booking_request_list_fragment)
            }

            spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedItem = parent?.getItemAtPosition(position).toString()
                    viewModel.filter(selectedItem)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
