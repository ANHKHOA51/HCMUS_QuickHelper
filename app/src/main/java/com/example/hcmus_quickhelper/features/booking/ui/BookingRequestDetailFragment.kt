package com.example.hcmus_quickhelper.features.booking.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.hcmus_quickhelper.core.utils.toSmartTime
import com.example.hcmus_quickhelper.databinding.FragmentBookingRequestDetailBinding
import com.example.hcmus_quickhelper.features.booking.datasource.MockBookingRequestDataSource
import com.example.hcmus_quickhelper.features.booking.model.BookingRequest

class BookingRequestDetailFragment : Fragment() {

    private var _binding: FragmentBookingRequestDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingRequestDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bookingId = arguments?.getInt("bookingId") ?: -1
        if (bookingId != -1) {
            loadBookingDetail(bookingId)
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun loadBookingDetail(id: Int) {
        // Mock data loading
        val booking = MockBookingRequestDataSource().getAll().find { it.id == id }
        booking?.let {
            displayBooking(it)
        }
    }

    private fun displayBooking(booking: BookingRequest) {
        binding.apply {
            tvUserName.text = booking.customerName
            tvPhone.text = booking.customerPhone
            tvDateBooking.text = booking.customerRating.toString()
            
            tvServiceName.text = booking.serviceName
            tvAddress.text = booking.address
            tvDateTime.text = booking.schedule.toSmartTime()
            
            // Note: ivUserAvatar, imgService, imgLocation, imgCalendar should be loaded with Glide if needed
            // For now they use default src from XML
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
