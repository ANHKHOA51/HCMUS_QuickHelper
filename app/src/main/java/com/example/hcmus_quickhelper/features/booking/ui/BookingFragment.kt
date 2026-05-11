package com.example.hcmus_quickhelper.features.booking.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.hcmus_quickhelper.R
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import coil.load
import com.example.hcmus_quickhelper.core.auth.SessionManager
import com.example.hcmus_quickhelper.core.model.Booking
import com.example.hcmus_quickhelper.features.booking.viewmodel.BookingViewModel
import com.google.android.material.imageview.ShapeableImageView
import java.text.SimpleDateFormat
import java.util.Locale

class BookingFragment : Fragment(R.layout.fragment_booking) {

    private lateinit var viewModel: BookingViewModel
    private var currentHelperId: Int = -1
    private val calendar = Calendar.getInstance()

    // Views
    private lateinit var ivHelperAvatar: ShapeableImageView
    private lateinit var tvHelperName: TextView
    private lateinit var tvHelperRating: TextView
    private lateinit var spinnerServices: Spinner
    private lateinit var btnDate: Button
    private lateinit var btnTime: Button
    private lateinit var edtQuantity: EditText
    private lateinit var edtNote: EditText
    private lateinit var tvTotalPrice: TextView
    private lateinit var confirmBtn: Button
    // Map views
    private lateinit var webView: WebView
    private lateinit var edtSearch: EditText
    private lateinit var tvLocationName: TextView
    private lateinit var tvLocationAddress: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[BookingViewModel::class.java]
        currentHelperId = arguments?.getInt("helperId") ?: -1

        initViews(view)
        setupMap()
        setupListeners()
        observeViewModel()

        if (currentHelperId != -1) {
            viewModel.loadHelperInfo(currentHelperId)
        }
    }

    private fun initViews(view: View) {
        ivHelperAvatar = view.findViewById(R.id.ivHelperAvatar)
        tvHelperName = view.findViewById(R.id.tvHelperName)
        tvHelperRating = view.findViewById(R.id.tvHelperRating)
        spinnerServices = view.findViewById(R.id.spinnerServices)
        btnDate = view.findViewById(R.id.btnDate)
        btnTime = view.findViewById(R.id.btnTime)
        edtQuantity = view.findViewById(R.id.edtQuantity)
        edtNote = view.findViewById(R.id.edtNote)
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice)
        confirmBtn = view.findViewById(R.id.confirmBtn)
        webView = view.findViewById(R.id.webViewMap)
        edtSearch = view.findViewById(R.id.edtSearch)
        tvLocationName = view.findViewById(R.id.tvLocationName)
        tvLocationAddress = view.findViewById(R.id.tvLocationAddress)

        // Localization
        btnDate.text = getString(R.string.select_date)
        btnTime.text = getString(R.string.select_time)
        confirmBtn.text = getString(R.string.confirm_booking)
        view.findViewById<TextView>(R.id.tvTitle)?.text = getString(R.string.booking_title)

        view.findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observeViewModel() {
        viewModel.helperData.observe(viewLifecycleOwner) { helper ->
            tvHelperName.text = helper.fullname
            tvHelperRating.text = helper.rating.toString()
            ivHelperAvatar.load(helper.avatarUrl) {
                placeholder(R.drawable.default_avt)
                error(R.drawable.default_avt)
            }

            // Setup Dropdown
            val serviceNames = helper.services.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, serviceNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerServices.adapter = adapter

            spinnerServices.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    viewModel.selectedService = helper.services[position]
                    viewModel.calculateTotal()
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        viewModel.totalPrice.observe(viewLifecycleOwner) { total ->
            tvTotalPrice.text = getString(R.string.total_price, "${total.toLong()}đ")
        }
    }

    private fun setupListeners() {
        btnDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    btnDate.text = dateFormat.format(calendar.time)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        btnTime.setOnClickListener {
            TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                    btnTime.text = timeFormat.format(calendar.time)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true // Dùng 24h format
            ).show()
        }

        edtQuantity.doAfterTextChanged { text ->
            val qty = text.toString().toIntOrNull() ?: 1
            viewModel.quantityHours = qty
            viewModel.calculateTotal()
        }

        confirmBtn.setOnClickListener {
            // Timestamp
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val scheduleString = format.format(calendar.time)

            val addressString = tvLocationAddress.text.toString()
            val noteString = edtNote.text.toString()

            // Fix for id
            val currentCustomerId = SessionManager.currentUser.asLiveData().value?.id ?: -1

            viewModel.createBooking(
                customerId = currentCustomerId,
                helperId = currentHelperId,
                schedule = scheduleString,
                address = addressString,
                note = noteString,
                onSuccess = { createdBookingId ->
                    val selectedServiceName = viewModel.selectedService?.name ?: "Dịch vụ"
                    val bundle = Bundle().apply {
                        putInt("bookingId", createdBookingId)
                        putString("serviceName", selectedServiceName)
                    }
                    // ĐIỀU HƯỚNG SANG TRACKING HELPERS THAY VÌ PAYMENT
                    findNavController().navigate(R.id.action_booking_to_tracking, bundle)
                }
            )
        }
    }

    private fun setupMap() {
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()
        webView.addJavascriptInterface(WebAppInterface(), "AndroidInterface")
        webView.loadUrl("file:///android_asset/map.html")

        edtSearch.setOnEditorActionListener { _, _, _ ->
            val query = edtSearch.text.toString()
            if (query.isNotEmpty()) {
                webView.loadUrl("javascript:searchLocation('$query')")
            }
            true
        }
    }

    inner class WebAppInterface {
        @JavascriptInterface
        fun updateLocationInfo(name: String, displayName: String) {
            activity?.runOnUiThread {
                tvLocationName.text = name
                tvLocationAddress.text = displayName
            }
        }
    }
}
