package com.example.hcmus_quickhelper.features.booking.ui

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.databinding.FragmentBookingRequestListBinding
import com.example.hcmus_quickhelper.features.booking.datasource.BookingDataSource
import com.example.hcmus_quickhelper.features.booking.datasource.MockBookingRequestDataSource
import com.example.hcmus_quickhelper.features.booking.repository.BookingRepository
import com.example.hcmus_quickhelper.features.booking.repository.BookingRequestRepository
import com.example.hcmus_quickhelper.features.booking.viewmodel.BookingRequestTab
import com.example.hcmus_quickhelper.features.booking.viewmodel.BookingRequestViewModel

class BookingRequestListFragment : Fragment() {

    private var _binding: FragmentBookingRequestListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: BookingRequestViewModel
    private val bookingAdapter by lazy { BookingRequestAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingRequestListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        setupObserve()
        setupTabListeners()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupViewModel() {
        val repository = BookingRequestRepository(MockBookingRequestDataSource())
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return BookingRequestViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[BookingRequestViewModel::class.java]

        viewModel.loadBookings()
    }

    private fun setupRecyclerView() {
        binding.recyclerViewBooking.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = bookingAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupObserve() {
        viewModel.currentTab.observe(viewLifecycleOwner) { tab ->
            updateTabUI(tab)
            viewModel.loadBookings()
        }

        viewModel.filterBooking.observe(viewLifecycleOwner) { list ->
            list?.let {
                bookingAdapter.updateData(it)
            }
        }
    }

    private fun setupTabListeners() {
        binding.cvTabNewest.setOnClickListener {
            viewModel.selectTab(BookingRequestTab.NEWEST)
        }
        binding.cvTabUpcoming.setOnClickListener {
            viewModel.selectTab(BookingRequestTab.UPCOMING)
        }
        binding.cvTabHistory.setOnClickListener {
            viewModel.selectTab(BookingRequestTab.COMPLETED)
        }
    }

    private fun updateTabUI(tab: BookingRequestTab) {
        val inactiveBgColor = Color.TRANSPARENT
        val inactiveTextColor = ContextCompat.getColor(requireContext(), R.color.black_light)

        val activeBgColor = Color.WHITE
        val activeTextColor = ContextCompat.getColor(requireContext(), R.color.orange_primary)

        // Reset all tabs
        val tabs = listOf(
            Triple(binding.cvTabNewest, binding.tvTabNewest, BookingRequestTab.NEWEST),
            Triple(binding.cvTabUpcoming, binding.tvTabUpcoming, BookingRequestTab.UPCOMING),
            Triple(binding.cvTabHistory, binding.tvTabHistory, BookingRequestTab.COMPLETED)
        )

        tabs.forEach { (cv, tv, _) ->
            cv.setCardBackgroundColor(inactiveBgColor)
            cv.cardElevation = 0f
            tv.setTextColor(inactiveTextColor)
            tv.setTypeface(null, Typeface.NORMAL)
        }

        // Set active tab
        val activeTab = tabs.find { it.third == tab }
        activeTab?.let { (cv, tv, _) ->
            cv.setCardBackgroundColor(activeBgColor)
            cv.cardElevation = 4f
            tv.setTextColor(activeTextColor)
            tv.setTypeface(null, Typeface.BOLD)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
