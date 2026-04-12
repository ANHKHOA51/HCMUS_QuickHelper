package com.example.hcmus_quickhelper.features.booking.ui

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hcmus_quickhelper.features.booking.viewmodel.BookingHistoryViewModel
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.features.booking.datasource.BookingLocalDataSource
import com.example.hcmus_quickhelper.features.booking.repository.BookingRepository
import com.example.hcmus_quickhelper.features.booking.viewmodel.BookingTab

class BookingHistoryFragment : Fragment() {

    private lateinit var viewModel: BookingHistoryViewModel
    private lateinit var adapter: BookingHistoryAdapter
    private lateinit var rvHistories: RecyclerView

    // Khai báo các view của Tabs
    private lateinit var cvTabOngoing: CardView
    private lateinit var tvTabOngoing: TextView
    private lateinit var cvTabCompleted: CardView
    private lateinit var tvTabCompleted: TextView
    private lateinit var cvTabCancelled: CardView
    private lateinit var tvTabCancelled: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_booking_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDependencies()
        initViews(view)
        setupRecyclerView()
        setupTabListeners()
        observeViewModel()

        viewModel.loadHistories()
    }

    private fun setupDependencies() {
        val dataSource = BookingLocalDataSource()
        val repository = BookingRepository(dataSource)
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return BookingHistoryViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[BookingHistoryViewModel::class.java]
    }

    private fun initViews(view: View) {
        rvHistories = view.findViewById(R.id.rvHistories)

        cvTabOngoing = view.findViewById(R.id.cvTabOngoing)
        tvTabOngoing = view.findViewById(R.id.tvTabOngoing)
        cvTabCompleted = view.findViewById(R.id.cvTabCompleted)
        tvTabCompleted = view.findViewById(R.id.tvTabCompleted)
        cvTabCancelled = view.findViewById(R.id.cvTabCancelled)
        tvTabCancelled = view.findViewById(R.id.tvTabCancelled)
    }

    private fun setupRecyclerView() {
        adapter = BookingHistoryAdapter()
        rvHistories.layoutManager = LinearLayoutManager(requireContext())
        rvHistories.adapter = adapter
    }

    private fun setupTabListeners() {
        cvTabOngoing.setOnClickListener { viewModel.selectTab(BookingTab.ONGOING) }
        cvTabCompleted.setOnClickListener { viewModel.selectTab(BookingTab.COMPLETED) }
        cvTabCancelled.setOnClickListener { viewModel.selectTab(BookingTab.CANCELLED) }
    }

    private fun observeViewModel() {
        // Observe sự thay đổi của danh sách được filter
        viewModel.filteredHistories.observe(viewLifecycleOwner) { histories ->
            adapter.updateData(histories)
        }

        // Observe sự thay đổi của Tab để cập nhật UI mượt mà
        viewModel.currentTab.observe(viewLifecycleOwner) { selectedTab ->
            updateTabUI(selectedTab)
        }
    }

    private fun updateTabUI(selectedTab: BookingTab) {
        // 1. Đưa tất cả về trạng thái Inactive (Màu xám, nền trong suốt)
        val inactiveBgColor = Color.TRANSPARENT
        val inactiveTextColor = Color.parseColor("#888888")

        cvTabOngoing.setCardBackgroundColor(inactiveBgColor)
        cvTabOngoing.cardElevation = 0f
        tvTabOngoing.setTextColor(inactiveTextColor)
        tvTabOngoing.setTypeface(null, Typeface.NORMAL)

        cvTabCompleted.setCardBackgroundColor(inactiveBgColor)
        cvTabCompleted.cardElevation = 0f
        tvTabCompleted.setTextColor(inactiveTextColor)
        tvTabCompleted.setTypeface(null, Typeface.NORMAL)

        cvTabCancelled.setCardBackgroundColor(inactiveBgColor)
        cvTabCancelled.cardElevation = 0f
        tvTabCancelled.setTextColor(inactiveTextColor)
        tvTabCancelled.setTypeface(null, Typeface.NORMAL)

        // 2. Highlight Tab đang được chọn (Nền trắng, chữ cam đất, in đậm)
        val activeBgColor = Color.WHITE
        val activeTextColor = Color.parseColor("#8D4F28")

        when (selectedTab) {
            BookingTab.ONGOING -> {
                cvTabOngoing.setCardBackgroundColor(activeBgColor)
                cvTabOngoing.cardElevation = 4f
                tvTabOngoing.setTextColor(activeTextColor)
                tvTabOngoing.setTypeface(null, Typeface.BOLD)
            }
            BookingTab.COMPLETED -> {
                cvTabCompleted.setCardBackgroundColor(activeBgColor)
                cvTabCompleted.cardElevation = 4f
                tvTabCompleted.setTextColor(activeTextColor)
                tvTabCompleted.setTypeface(null, Typeface.BOLD)
            }
            BookingTab.CANCELLED -> {
                cvTabCancelled.setCardBackgroundColor(activeBgColor)
                cvTabCancelled.cardElevation = 4f
                tvTabCancelled.setTextColor(activeTextColor)
                tvTabCancelled.setTypeface(null, Typeface.BOLD)
            }
        }
    }
}