package com.example.hcmus_quickhelper.features.rating.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.databinding.FragmentRatingBinding
import com.example.hcmus_quickhelper.databinding.FragmentReceiptBinding
import com.example.hcmus_quickhelper.features.booking.datasource.BookingDataSource
import com.example.hcmus_quickhelper.features.booking.repository.BookingRepository
import com.example.hcmus_quickhelper.features.payment.datasource.MockPaymentDataSource
import com.example.hcmus_quickhelper.features.payment.repository.PaymentRepository
import com.example.hcmus_quickhelper.features.payment.viewmodel.PaymentViewModel
import com.example.hcmus_quickhelper.features.rating.datasource.MockRatingDataSource
import com.example.hcmus_quickhelper.features.rating.datasource.RatingDataSource
import com.example.hcmus_quickhelper.features.rating.repository.RatingRepository
import com.example.hcmus_quickhelper.features.rating.viewmodel.RatingViewModel

class RatingFragment : Fragment(R.layout.fragment_rating) {

    private lateinit var viewModel: RatingViewModel

    private var _binding: FragmentRatingBinding? = null
    private val binding get() = _binding!!

    private var bookingId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRatingBinding.inflate(inflater, container, false)

        bookingId = arguments?.getInt("booking_id", -1) ?: -1

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupObservers()
        loadData()

        binding.btnSendRating.setOnClickListener { sendRating() }
    }

    fun setupViewModel() {
        val ratingRepository = RatingRepository(RatingDataSource())
        val bookingRepository = BookingRepository(BookingDataSource())

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return RatingViewModel(ratingRepository, bookingRepository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[RatingViewModel::class.java]
    }

    fun setupObservers() {
        viewModel.rating.observe(viewLifecycleOwner) { rating ->
            if (rating != null) {
                Log.d("DEBUG", "${rating.point}")
                binding.ratingBar.rating = rating.point.toFloat()
            }
        }

        viewModel.booking.observe(viewLifecycleOwner) {booking ->
            if (booking != null) {
                binding.tvServiceId.text = "Mã dịch vụ: #${booking.serviceId}"

            }
        }
    }

    fun loadData() {
        viewModel.loaData(bookingId)
    }

    fun sendRating() {
        val point = binding.ratingBar.rating.toInt()
        val comment = binding.etComment.text.toString()

        viewModel.submitRating(point, comment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}