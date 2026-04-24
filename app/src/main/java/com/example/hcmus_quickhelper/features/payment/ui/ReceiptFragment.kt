package com.example.hcmus_quickhelper.features.payment.ui

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.core.auth.SessionManager
import com.example.hcmus_quickhelper.core.model.UserRole
import com.example.hcmus_quickhelper.core.utils.MoneyUtils
import com.example.hcmus_quickhelper.core.utils.toSmartTime
import com.example.hcmus_quickhelper.databinding.FragmentReceiptBinding
import com.example.hcmus_quickhelper.features.payment.datasource.PaymentDataSource
import com.example.hcmus_quickhelper.features.payment.repository.PaymentRepository
import com.example.hcmus_quickhelper.features.payment.viewmodel.ReceiptViewModel
import kotlinx.coroutines.flow.drop
import java.io.OutputStream

class ReceiptFragment : Fragment(R.layout.fragment_receipt) {

    private lateinit var viewModel: ReceiptViewModel

    private var _binding: FragmentReceiptBinding? = null
    private val binding get() = _binding!!

    private var paymentId: Int = -1


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReceiptBinding.inflate(inflater, container, false)
        paymentId = arguments?.getInt("payment_id", -1) ?: -1
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupObservers()
        loadData()

        binding.btnGoToRating.setOnClickListener { handleGoToRating(viewModel.payment.value?.bookingId) }
        binding.btnBack.setOnClickListener { handleBack() }
        binding.btnBackToHome.setOnClickListener { handleBackHome() }
        binding.btnDownload.setOnClickListener { handleDownload() }
    }

    private fun setupViewModel() {
        val paymentRepository = PaymentRepository(PaymentDataSource())

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ReceiptViewModel(paymentRepository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[ReceiptViewModel::class.java]
    }

    private fun setupObservers() {
        viewModel.payment.observe(viewLifecycleOwner) { payment ->
            payment?.let {
                binding.tvPaymentId.text = "Mã hóa đơn: #${it.id}"
                binding.tvVoucherDiscount.text = "-${MoneyUtils.formatVietnameseCurrency(it.voucher?.discount ?: 0.0)}"

                binding.tvDate.text = it.booking?.schedule?.toSmartTime()
                binding.tvServicePrice.text = "${MoneyUtils.formatVietnameseCurrency(it.booking?.totalPrice ?: 0.0)}"

                binding.tvTotalPrice.text = "${MoneyUtils.formatVietnameseCurrency(it.amount)}"

                binding.tvServiceName.text = it.booking?.service?.name ?: "Dịch vụ"

                if(SessionManager.currentUser.value?.role == UserRole.CUSTOMER.toString()) {
                    binding.btnGoToRating.visibility = View.VISIBLE
                } else {
                    binding.btnGoToRating.visibility = View.GONE
                }
            }
        }
    }

    private  fun loadData() {
        viewModel.loadData(paymentId)
    }

    private fun handleDownload() {
        // 1. Tạm thời ẩn các thành phần không muốn có trong ảnh chụp
        val buttonsToHide = listOf(
            binding.btnGoToRating,
            binding.btnBackToHome,
            binding.btnBack,
            binding.btnDownload
        )
        buttonsToHide.forEach { it.visibility = View.GONE }

        // 2. Chụp ảnh màn hình (Lấy view chứa toàn bộ nội dung Fragment)
        val viewToCapture = binding.root
        val bitmap = Bitmap.createBitmap(viewToCapture.width, viewToCapture.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        viewToCapture.draw(canvas)

        // 3. Hiện lại các nút bấm ngay lập tức
        buttonsToHide.forEach { it.visibility = View.VISIBLE }

        if(SessionManager.currentUser.value?.role == UserRole.CUSTOMER.toString()) {
            binding.btnGoToRating.visibility = View.VISIBLE
        } else {
            binding.btnGoToRating.visibility = View.GONE
        }

        // 4. Lưu Bitmap vào thư viện ảnh của thiết bị
        saveBitmapToGallery(bitmap)
    }

    private fun saveBitmapToGallery(bitmap: Bitmap) {
        val filename = "Receipt_${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/QuickHelper")
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val contentResolver = requireContext().contentResolver
        val imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        imageUri?.let { uri ->
            try {
                fos = contentResolver.openOutputStream(uri)
                fos?.use {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    contentResolver.update(uri, contentValues, null, null)
                }
                Toast.makeText(requireContext(), "Hóa đơn đã được lưu vào máy!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Lỗi khi lưu ảnh!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleBackHome() {
        if(SessionManager.currentUser.value?.role == UserRole.CUSTOMER.toString()) {
            findNavController().navigate(R.id.action_receipt_fragment_to_home)
        } else {
            findNavController().navigate(R.id.action_receipt_fragment_to_dashboard_helper_fragment)
        }
    }

    private fun handleGoToRating(bookingId: Int?) {
        val navController = findNavController()

        val bundle = Bundle().apply {
            putInt("booking_id", bookingId ?: -1)
        }

        navController.navigate(R.id.action_receipt_fragment_to_rating_fragment, bundle)
    }

    private fun handleBack() {
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
