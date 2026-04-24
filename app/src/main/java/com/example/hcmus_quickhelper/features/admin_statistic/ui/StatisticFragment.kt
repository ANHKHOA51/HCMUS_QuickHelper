package com.example.hcmus_quickhelper.features.admin_statistic.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.core.auth.SessionManager
import com.example.hcmus_quickhelper.databinding.FragmentAdminStatisticBinding
import com.example.hcmus_quickhelper.features.admin_statistic.viewmodel.StatisticViewModel
import java.text.NumberFormat
import java.util.Locale
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.data.Entry

class StatisticFragment : Fragment(R.layout.fragment_admin_statistic) {

    private lateinit var binding: FragmentAdminStatisticBinding
    private lateinit var viewModel: StatisticViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAdminStatisticBinding.bind(view)
        viewModel = ViewModelProvider(this)[StatisticViewModel::class.java]

        setupListeners()
        observeViewModel()
        setupChart();

        selectTab(0)
    }

    // config chart
    private fun setupChart() {
        binding.lineChartRevenue.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(false)

            // x - date
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            xAxis.textColor = Color.parseColor("#6C757D")
            xAxis.granularity = 1f

            // y - payment
            axisRight.isEnabled = false
            axisLeft.setDrawGridLines(true)
            axisLeft.textColor = Color.parseColor("#6C757D")
            axisLeft.axisMinimum = 0f
        }
    }

    private fun setupListeners() {
//        binding.btnBack.setOnClickListener { findNavController().navigateUp() }

        // Chuyển Tabs
        binding.cvTabOverview.setOnClickListener { selectTab(0) }
        binding.cvTabRevenue.setOnClickListener { selectTab(1) }
        binding.cvTabUX.setOnClickListener { selectTab(2) }

        binding.btnManagePayment.setOnClickListener { findNavController().navigate(R.id.action_fragment_admin_statistic_to_payment_admin_fragment) }
        binding.btnManageVoucher.setOnClickListener { findNavController().navigate(R.id.action_fragment_admin_statistic_to_voucher_management_fragment) }

        binding.btnLogout.setOnClickListener {
            SessionManager.logout()
            findNavController().navigate(R.id.action_fragment_admin_statistic_to_login_fragment)
        }
    }

    private fun observeViewModel() {
        // Observers Tab 1
        viewModel.totalUsers.observe(viewLifecycleOwner) { binding.tvTotalUsers.text = it.toString() }
        viewModel.totalHelpers.observe(viewLifecycleOwner) { binding.tvTotalHelpers.text = it.toString() }
        viewModel.totalServices.observe(viewLifecycleOwner) { binding.tvTotalServices.text = it.toString() }

        // Observers Tab 2
        viewModel.totalRevenue.observe(viewLifecycleOwner) {
            binding.tvTotalRevenue.text = formatCurrency(it)
        }
        viewModel.topRevenueHelperName.observe(viewLifecycleOwner) { binding.tvTopRevenueHelperName.text = it }
        viewModel.topRevenueValue.observe(viewLifecycleOwner) {
            binding.tvTopRevenueHelperValue.text = formatCurrency(it)
        }

        // Observers Tab 3
        viewModel.appAverageRating.observe(viewLifecycleOwner) {
            binding.tvAppAverageRating.text = String.format(Locale.US, "%.1f / 5.0", it)
        }
        viewModel.topUxHelperName.observe(viewLifecycleOwner) { binding.tvTopUXHelperName.text = it }
        viewModel.topUxHelperRating.observe(viewLifecycleOwner) {
            binding.tvTopUXHelperRating.text = String.format(Locale.US, "%.1f", it)
        }

        viewModel.bottomUxHelperName.observe(viewLifecycleOwner) { binding.tvBottomUXHelperName.text = it }
        viewModel.bottomUxHelperRating.observe(viewLifecycleOwner) {
            binding.tvBottomUXHelperRating.text = String.format(Locale.US, "%.1f", it)
        }

        viewModel.weeklyRevenue.observe(viewLifecycleOwner) { data ->
            val entries = ArrayList<Entry>()
            val labels = ArrayList<String>()

            data.forEachIndexed { index, pair ->
                entries.add(Entry(index.toFloat(), pair.second))
                labels.add(pair.first)
            }

            // Set label cho trục X
            binding.lineChartRevenue.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            binding.lineChartRevenue.xAxis.labelCount = labels.size

            // Tạo dataSet và style đường vẽ
            val dataSet = LineDataSet(entries, "Doanh thu").apply {
                color = Color.parseColor("#E56B3D")
                lineWidth = 3f
                circleRadius = 5f
                setCircleColor(Color.parseColor("#E56B3D"))
                setDrawCircleHole(true)
                circleHoleColor = Color.WHITE
                valueTextColor = Color.parseColor("#212529")
                valueTextSize = 10f
                mode = LineDataSet.Mode.CUBIC_BEZIER
                setDrawFilled(true)
                fillColor = Color.parseColor("#FFF3E0")
                fillAlpha = 100
            }


            val lineData = LineData(dataSet)
            binding.lineChartRevenue.data = lineData
            binding.lineChartRevenue.invalidate()
        }
    }

    // Logic UI chuyển Tab VÀ Tải Dữ Liệu
    private fun selectTab(index: Int) {
        val unselectedColor = Color.parseColor("#00000000")
        val unselectedTextColor = Color.parseColor("#6C757D")
        val unselectedElevation = 0f

        // 1. Reset toàn bộ UI Tab Header về trạng thái chưa chọn
        binding.cvTabOverview.setCardBackgroundColor(unselectedColor)
        binding.tvTabOverview.setTextColor(unselectedTextColor)
        binding.cvTabOverview.cardElevation = unselectedElevation

        binding.cvTabRevenue.setCardBackgroundColor(unselectedColor)
        binding.tvTabRevenue.setTextColor(unselectedTextColor)
        binding.cvTabRevenue.cardElevation = unselectedElevation

        binding.cvTabUX.setCardBackgroundColor(unselectedColor)
        binding.tvTabUX.setTextColor(unselectedTextColor)
        binding.cvTabUX.cardElevation = unselectedElevation

        // 2. Ẩn toàn bộ Content Layouts
        binding.llTabOverview.visibility = View.GONE
        binding.llTabRevenue.visibility = View.GONE
        binding.llTabUX.visibility = View.GONE

        val selectedColor = Color.parseColor("#FFFFFF")
        val selectedTextColor = Color.parseColor("#E56B3D")
        val selectedElevation = 4f

        when (index) {
            0 -> {
                binding.cvTabOverview.setCardBackgroundColor(selectedColor)
                binding.tvTabOverview.setTextColor(selectedTextColor)
                binding.cvTabOverview.cardElevation = selectedElevation
                binding.llTabOverview.visibility = View.VISIBLE

                viewModel.fetchOverviewData()
            }
            1 -> {
                binding.cvTabRevenue.setCardBackgroundColor(selectedColor)
                binding.tvTabRevenue.setTextColor(selectedTextColor)
                binding.cvTabRevenue.cardElevation = selectedElevation
                binding.llTabRevenue.visibility = View.VISIBLE

                viewModel.fetchRevenueData()
            }
            2 -> {
                binding.cvTabUX.setCardBackgroundColor(selectedColor)
                binding.tvTabUX.setTextColor(selectedTextColor)
                binding.cvTabUX.cardElevation = selectedElevation
                binding.llTabUX.visibility = View.VISIBLE

                viewModel.fetchUXData()
            }
        }
    }

    // Tiện ích format Tiền Tệ
    private fun formatCurrency(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        return format.format(amount)
    }
}