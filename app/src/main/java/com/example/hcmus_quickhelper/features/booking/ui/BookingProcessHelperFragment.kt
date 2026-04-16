package com.example.hcmus_quickhelper.features.booking.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.hcmus_quickhelper.core.model.BookingStatus
import com.example.hcmus_quickhelper.core.utils.toRemainingTime
import com.example.hcmus_quickhelper.databinding.FragmentBookingProcessHelperBinding
import com.example.hcmus_quickhelper.features.booking.datasource.MockBookingRequestDataSource
import com.example.hcmus_quickhelper.features.booking.model.BookingRequest
import com.example.hcmus_quickhelper.features.booking.repository.BookingRequestRepository
import com.example.hcmus_quickhelper.features.booking.viewmodel.BookingProcessHelperViewModel

class BookingProcessHelperFragment : Fragment() {

    private var _binding: FragmentBookingProcessHelperBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: BookingProcessHelperViewModel

    private val evidenceAdapter: EvidenceImageAdapter by lazy {
        EvidenceImageAdapter { position ->
            viewModel.removeEvidence(position)
        }
    }

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.addEvidence(uris)
        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    private var bookingId = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingProcessHelperBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        setupObserve()
        setupListeners()

        bookingId = arguments?.getInt("bookingId") ?: -1
        if (bookingId != -1) {
            viewModel.loadBooking(bookingId)
        }
    }

    private fun setupViewModel() {
        val repository = BookingRequestRepository(MockBookingRequestDataSource())
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return BookingProcessHelperViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[BookingProcessHelperViewModel::class.java]
    }

    private fun setupRecyclerView() {
        binding.rvEvidenceImages.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = evidenceAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupObserve() {
        viewModel.booking.observe(viewLifecycleOwner) { booking ->
            booking?.let { 
                displayBooking(it)
                updateUIBasedOnStatus(it.status)
                updateCountdown(it.schedule)
            }
        }

        viewModel.imageEvidence.observe(viewLifecycleOwner) { uris ->
            evidenceAdapter.updateImages(uris)
        }
    }

    private fun updateUIBasedOnStatus(status: String) {
        when (status) {
            BookingStatus.CONFIRMED.toString() -> {
                binding.btnStartWork.visibility = View.VISIBLE
                binding.btnCompleteWork.visibility = View.GONE
                // Khi chưa bắt đầu thì chưa cho thêm ảnh
                binding.cardEvidence.visibility = View.GONE
            }
            BookingStatus.IN_PROGRESS.toString() -> {
                binding.btnStartWork.visibility = View.GONE
                binding.btnCompleteWork.visibility = View.VISIBLE
                binding.cardEvidence.visibility = View.VISIBLE
            }
            else -> {
                binding.btnStartWork.visibility = View.GONE
                binding.btnCompleteWork.visibility = View.GONE
                binding.cardEvidence.visibility = View.VISIBLE
            }
        }
    }

    private fun updateCountdown(schedule: String) {
        runnable = object : Runnable {
            override fun run() {

                binding.tvCountdownBanner.text = schedule.toRemainingTime()

                handler.postDelayed(this, 60000)
            }
        }
        handler.post(runnable)
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnAddImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnStartWork.setOnClickListener {
            // Cập nhật trạng thái sang IN_PROGRESS trong ViewModel
            viewModel.updateBookingStatus(BookingStatus.IN_PROGRESS.toString())
        }

        binding.btnCompleteWork.setOnClickListener {
            // Logic hoàn tất công việc
            viewModel.updateBookingStatus(BookingStatus.COMPLETED.toString())
        }

        binding.btnChat.setOnClickListener {
            // Logic mở chat
        }
    }

    private fun displayBooking(booking: BookingRequest) {
        binding.apply {
            tvUserName.text = booking.customerName
            tvPhone.text = booking.customerPhone
            tvServiceName.text = booking.serviceName
            tvAddress.text = booking.address
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
