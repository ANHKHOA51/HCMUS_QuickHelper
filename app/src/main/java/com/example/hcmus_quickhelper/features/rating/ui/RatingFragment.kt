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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRatingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupObservers()

        binding.btnSendRating.setOnClickListener { sendRating() }
    }

    fun setupViewModel() {
        val dataSource = RatingDataSource()
        val repository = RatingRepository(dataSource)

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return RatingViewModel(repository) as T
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
    }

    fun sendRating() {
        val point = binding.ratingBar.rating.toInt()
        val comment = binding.etComment.text

        Log.d("DEBUG", "Point: $point, Comment: $comment")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}