package com.example.hcmus_quickhelper.features.booking.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.core.model.Booking
import com.example.hcmus_quickhelper.core.model.BookingStatus
import com.example.hcmus_quickhelper.core.utils.MoneyUtils
import com.example.hcmus_quickhelper.core.utils.toMessageTime
import com.example.hcmus_quickhelper.databinding.FragmentBookingRequestDetailBinding
import com.example.hcmus_quickhelper.features.booking.datasource.BookingDataSource
import com.example.hcmus_quickhelper.features.booking.repository.BookingRepository
import com.example.hcmus_quickhelper.features.booking.viewmodel.BookingRequestDetailViewModel

class BookingRequestDetailFragment : Fragment() {

    private var _binding: FragmentBookingRequestDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: BookingRequestDetailViewModel
    private var bookingId: Int = -1

    private var helperId: Int = 5


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingRequestDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bookingId = arguments?.getInt("bookingId") ?: -1

        setupViewModel()
        setupObserve()
        setupListeners()

        if (bookingId != -1) {
            viewModel.loadBooking(bookingId)
        }
    }

    private fun setupViewModel() {
        val repository = BookingRepository(BookingDataSource())
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return BookingRequestDetailViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[BookingRequestDetailViewModel::class.java]
    }

    private fun setupObserve() {
        viewModel.booking.observe(viewLifecycleOwner) { booking ->
            booking?.let { displayBooking(it) }
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnAccept.setOnClickListener {
            viewModel.acceptBooking(helperId)
             findNavController().popBackStack()
        }

        binding.btnDecline.setOnClickListener {
            viewModel.rejectBooking(helperId)
             findNavController().popBackStack()
        }

        binding.btnChat.setOnClickListener {
            val convId = viewModel.conversationId.value
            if (convId != null) {
                val customer = viewModel.booking.value?.customer
                val bundle = Bundle().apply {
                    putInt("conversationId", convId)
                    putString("senderName", customer?.fullname)
                    putString("senderAvtUrl", customer?.avatarUrl)
                }
                findNavController().navigate(R.id.action_booking_request_detail_fragment_to_chat_fragment, bundle)
            }
        }
    }

    private fun displayBooking(booking: Booking) {
        binding.apply {
            tvUserName.text = booking.customer?.fullname
            tvPhone.text = booking.customer?.phone
            tvIncomeAmount.text = MoneyUtils.formatVietnameseCurrency(booking.totalPrice)
            tvServiceName.text = booking.service?.name
            tvAddress.text = booking.address
            tvDateTime.text = booking.schedule.toMessageTime()
            tvCustomerNote.text = booking.note

            if(booking.status == BookingStatus.REJECTED.toString()) {
                btnAccept.visibility = View.GONE
                btnDecline.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
