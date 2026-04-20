package com.example.hcmus_quickhelper.features.booking.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.databinding.FragmentBookingTrackinghelpersBinding
import com.example.hcmus_quickhelper.features.booking.viewmodel.BookingTrackingViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class BookingTrackingHelpersFragment : Fragment(R.layout.fragment_booking_trackinghelpers) {

    private lateinit var binding: FragmentBookingTrackinghelpersBinding
    private lateinit var viewModel: BookingTrackingViewModel
    private lateinit var evidenceAdapter: EvidenceViewerAdapter

    private var currentBookingId: Int = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBookingTrackinghelpersBinding.bind(view)
        viewModel = ViewModelProvider(this)[BookingTrackingViewModel::class.java]

        val serviceNameFromBundle = arguments?.getString("serviceName")
        binding.tvServiceName.text = "Dịch vụ: $serviceNameFromBundle"

        currentBookingId = arguments?.getInt("bookingId") ?: -1
        setupRecyclerView()

        setupListeners()
        observeViewModel()

        if (currentBookingId != -1) {
            viewModel.loadData(currentBookingId)
        }
    }

    private fun setupRecyclerView() {
        evidenceAdapter = EvidenceViewerAdapter(emptyList()) { imageUrl ->
            showFullScreenImage(imageUrl)
        }

        binding.rvEvidences.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = evidenceAdapter
        }
    }
    private fun showFullScreenImage(imageUrl: String) {
        // Khởi tạo Dialog với theme Fullscreen để chiếm trọn màn hình
        val dialog = android.app.Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.setContentView(R.layout.dialog_evidence_image_viewer)


        val ivFullImage = dialog.findViewById<android.widget.ImageView>(R.id.ivFullImage)
        val btnClose = dialog.findViewById<android.widget.ImageButton>(R.id.btnClose)

        // Load ảnh to vào Dialog bằng Coil
        ivFullImage.load(imageUrl) {
            crossfade(true)
            placeholder(R.drawable.ic_launcher_background)
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        // Hiển thị Dialog
        dialog.show()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }

        binding.btnBackToHome.setOnClickListener {
            // Đã bị reject, bấm về Home là xoá conservation luôn
            viewModel.deleteConversationIfRejected(currentBookingId)
            findNavController().navigate(R.id.action_tracking_to_home)
        }

        binding.btnPayment.setOnClickListener {
            // Chuyển qua trang thanh toán, truyền bookingId qua
            val bundle = Bundle().apply { putInt("bookingId", currentBookingId) }
            findNavController().navigate(R.id.action_tracking_to_payment, bundle)
        }

        binding.btnChat.setOnClickListener {
            val convId = viewModel.conversationId.value
            if (convId != null) {
                val helper = viewModel.booking.value?.helper
                val bundle = Bundle().apply {
                    putInt("conversationId", convId)
                    putString("senderName", helper?.fullname)
                    putString("senderAvtUrl", helper?.avatarUrl)
                }
                findNavController().navigate(R.id.action_tracking_to_chat, bundle)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.booking.observe(viewLifecycleOwner) { booking ->
            booking.helper?.let { helper ->
                binding.tvHelperName.text = helper.fullname
                binding.ivHelperAvatar.load(helper.avatarUrl) {
                    placeholder(R.drawable.default_avt)
                }
            }

            // lưu định dạng yyyy-MM-dd'T'HH:mm:ss
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())
                val date = inputFormat.parse(booking.schedule)
                binding.tvDateTime.text = "Thời gian: ${outputFormat.format(date)}"
            } catch (e: Exception) {
                binding.tvDateTime.text = "Thời gian: ${booking.schedule}"
            }

            binding.tvAddress.text = "Địa điểm: ${booking.address}"
            binding.tvQuantity.text = "Số giờ làm: ${booking.quantity} giờ"
            binding.tvNote.text = "Ghi chú: ${booking.note ?: "Không có"}"

            val currentStatus = booking.status.uppercase(Locale.ROOT).trim()
            when (currentStatus) {
                "PENDING" -> {
                    binding.tvStatusBanner.text = "Đang chờ xác nhận"
                    binding.tvStatusBanner.setBackgroundColor(Color.parseColor("#FFF3E0"))
                    binding.tvStatusBanner.setTextColor(Color.parseColor("#E65100"))

                    binding.cardEvidence.visibility = View.GONE
                    binding.btnPayment.visibility = View.GONE
                    binding.btnBackToHome.visibility = View.GONE
                }
                "REJECTED" -> {
                    binding.tvStatusBanner.text = "Booking đã bị từ chối"
                    binding.tvStatusBanner.setBackgroundColor(Color.parseColor("#FFEBEE"))
                    binding.tvStatusBanner.setTextColor(Color.parseColor("#D32F2F"))

                    binding.cardEvidence.visibility = View.GONE
                    binding.btnPayment.visibility = View.GONE
                    binding.btnBackToHome.visibility = View.VISIBLE
                }
                "IN_PROGRESS", "CONFIRMED" -> {
                    binding.tvStatusBanner.text = if (booking.status == "CONFIRMED") "Đã xác nhận - Chờ thực hiện" else "Đang thực hiện"
                    binding.tvStatusBanner.setBackgroundColor(Color.parseColor("#E3F2FD"))
                    binding.tvStatusBanner.setTextColor(Color.parseColor("#1565C0"))

                    binding.cardEvidence.visibility = View.VISIBLE
                    binding.btnPayment.visibility = View.GONE
                    binding.btnBackToHome.visibility = View.GONE
                }
                "COMPLETED" -> {
                    binding.tvStatusBanner.text = "Công việc đã hoàn tất"
                    binding.tvStatusBanner.setBackgroundColor(Color.parseColor("#E8F5E9"))
                    binding.tvStatusBanner.setTextColor(Color.parseColor("#2E7D32"))

                    binding.cardEvidence.visibility = View.VISIBLE
                    binding.btnPayment.visibility = View.VISIBLE
                    binding.btnBackToHome.visibility = View.GONE
                }
            }
        }

        viewModel.evidences.observe(viewLifecycleOwner) { evidencesList ->
            if (evidencesList.isNullOrEmpty()) {
                binding.tvNoEvidence.visibility = View.VISIBLE
                binding.rvEvidences.visibility = View.GONE
            } else {
                binding.tvNoEvidence.visibility = View.GONE
                binding.rvEvidences.visibility = View.VISIBLE

                // THÊM: Đổ dữ liệu vào Adapter
                evidenceAdapter.updateData(evidencesList)
            }
        }
    }
}