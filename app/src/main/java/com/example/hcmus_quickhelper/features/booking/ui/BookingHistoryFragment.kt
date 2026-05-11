package com.example.hcmus_quickhelper.features.booking.ui

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hcmus_quickhelper.features.booking.viewmodel.BookingHistoryViewModel
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.databinding.FragmentBookingHistoryBinding
import com.example.hcmus_quickhelper.features.booking.datasource.BookingDataSource
import com.example.hcmus_quickhelper.features.booking.repository.BookingRepository
import com.example.hcmus_quickhelper.features.booking.viewmodel.BookingTab

class BookingHistoryFragment : Fragment(R.layout.fragment_booking_history) {

    private lateinit var binding: FragmentBookingHistoryBinding
    private lateinit var viewModel: BookingHistoryViewModel
    private lateinit var adapter: BookingHistoryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBookingHistoryBinding.bind(view)

        setupTabNames()
        setupListeners()
        setupDependencies()
        setupRecyclerView()
        setupTabListeners()
        observeViewModel()

        viewModel.loadHistories()
    }

    private fun setupTabNames() {
        binding.tvTabOngoing.text = getString(R.string.tab_ongoing)
        binding.tvTabCompleted.text = getString(R.string.tab_completed)
        binding.tvTabCancelled.text = getString(R.string.tab_cancelled)
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupDependencies() {
        val dataSource = BookingDataSource()
        val repository = BookingRepository(dataSource)
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return BookingHistoryViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[BookingHistoryViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = BookingHistoryAdapter { bookingId, serviceName ->
            val bundle = Bundle().apply {
                putInt("bookingId", bookingId)
                putString("serviceName", serviceName)
            }
            findNavController().navigate(R.id.action_history_to_tracking, bundle)
        }

        binding.rvHistories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@BookingHistoryFragment.adapter
        }
    }

    private fun setupTabListeners() {
        binding.cvTabOngoing.setOnClickListener { viewModel.selectTab(BookingTab.ONGOING) }
        binding.cvTabCompleted.setOnClickListener { viewModel.selectTab(BookingTab.COMPLETED) }
        binding.cvTabCancelled.setOnClickListener { viewModel.selectTab(BookingTab.CANCELLED) }
    }

    private fun observeViewModel() {
        viewModel.filteredHistories.observe(viewLifecycleOwner) { histories ->
            adapter.updateData(histories)
        }
        viewModel.currentTab.observe(viewLifecycleOwner) { selectedTab ->
            updateTabUI(selectedTab)
        }
    }

    private fun updateTabUI(selectedTab: BookingTab) {
        val inactiveBgColor = Color.TRANSPARENT
        val inactiveTextColor = Color.parseColor("#888888")

        binding.cvTabOngoing.setCardBackgroundColor(inactiveBgColor)
        binding.cvTabOngoing.cardElevation = 0f
        binding.tvTabOngoing.setTextColor(inactiveTextColor)
        binding.tvTabOngoing.setTypeface(null, Typeface.NORMAL)

        binding.cvTabCompleted.setCardBackgroundColor(inactiveBgColor)
        binding.cvTabCompleted.cardElevation = 0f
        binding.tvTabCompleted.setTextColor(inactiveTextColor)
        binding.tvTabCompleted.setTypeface(null, Typeface.NORMAL)

        binding.cvTabCancelled.setCardBackgroundColor(inactiveBgColor)
        binding.cvTabCancelled.cardElevation = 0f
        binding.tvTabCancelled.setTextColor(inactiveTextColor)
        binding.tvTabCancelled.setTypeface(null, Typeface.NORMAL)

        // Highlight bằng màu cam #E56B3D
        val activeBgColor = Color.WHITE
        val activeTextColor = Color.parseColor("#E56B3D")

        when (selectedTab) {
            BookingTab.ONGOING -> {
                binding.cvTabOngoing.setCardBackgroundColor(activeBgColor)
                binding.cvTabOngoing.cardElevation = 4f
                binding.tvTabOngoing.setTextColor(activeTextColor)
                binding.tvTabOngoing.setTypeface(null, Typeface.BOLD)
            }
            BookingTab.COMPLETED -> {
                binding.cvTabCompleted.setCardBackgroundColor(activeBgColor)
                binding.cvTabCompleted.cardElevation = 4f
                binding.tvTabCompleted.setTextColor(activeTextColor)
                binding.tvTabCompleted.setTypeface(null, Typeface.BOLD)
            }
            BookingTab.CANCELLED -> {
                binding.cvTabCancelled.setCardBackgroundColor(activeBgColor)
                binding.cvTabCancelled.cardElevation = 4f
                binding.tvTabCancelled.setTextColor(activeTextColor)
                binding.tvTabCancelled.setTypeface(null, Typeface.BOLD)
            }
        }
    }
}
