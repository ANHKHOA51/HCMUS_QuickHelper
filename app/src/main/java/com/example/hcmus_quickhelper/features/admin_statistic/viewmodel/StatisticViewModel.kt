package com.example.hcmus_quickhelper.features.admin_statistic.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.features.admin_statistic.repository.StatisticRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StatisticViewModel : ViewModel() {
    private val repository = StatisticRepository()

    // tab 1
    private val _totalUsers = MutableLiveData<Int>()
    val totalUsers: LiveData<Int> get() = _totalUsers

    private val _totalHelpers = MutableLiveData<Int>()
    val totalHelpers: LiveData<Int> get() = _totalHelpers

    private val _totalServices = MutableLiveData<Int>()
    val totalServices: LiveData<Int> get() = _totalServices

    // tab 2
    private val _totalRevenue = MutableLiveData<Double>()
    val totalRevenue: LiveData<Double> get() = _totalRevenue

    private val _topRevenueHelperName = MutableLiveData<String>()
    val topRevenueHelperName: LiveData<String> get() = _topRevenueHelperName

    private val _topRevenueValue = MutableLiveData<Double>()
    val topRevenueValue: LiveData<Double> get() = _topRevenueValue

    // tab 3
    private val _appAverageRating = MutableLiveData<Double>()
    val appAverageRating: LiveData<Double> get() = _appAverageRating

    private val _topUxHelperName = MutableLiveData<String>()
    val topUxHelperName: LiveData<String> get() = _topUxHelperName

    private val _topUxHelperRating = MutableLiveData<Double>()
    val topUxHelperRating: LiveData<Double> get() = _topUxHelperRating

    private val _bottomUxHelperName = MutableLiveData<String>()
    val bottomUxHelperName: LiveData<String> get() = _bottomUxHelperName

    private val _bottomUxHelperRating = MutableLiveData<Double>()
    val bottomUxHelperRating: LiveData<Double> get() = _bottomUxHelperRating


    private val _weeklyRevenue = MutableLiveData<List<Pair<String, Float>>>()
    val weeklyRevenue: LiveData<List<Pair<String, Float>>> get() = _weeklyRevenue

    // fetch tab 1
    fun fetchOverviewData() {
        viewModelScope.launch {
            try {
                val users = repository.getUsers()
                val services = repository.getServices()

                val customers = users.filter { it.role.uppercase() == "CUSTOMER" }
                val helpers = users.filter { it.role.uppercase() == "HELPER" }

                _totalUsers.postValue(customers.size)
                _totalHelpers.postValue(helpers.size)
                _totalServices.postValue(services.size)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // fetch tab 2
    fun fetchRevenueData() {
        viewModelScope.launch {
            try {
                val payments = repository.getPayments()
                val bookings = repository.getBookings()
                val users = repository.getUsers() // Cần để lấy tên Helper
                val helpers = users.filter { it.role.uppercase() == "HELPER" }

                // Tính tổng doanh thu
                val totalRev = payments.sumOf { it.amount }
                _totalRevenue.postValue(totalRev)

                // Tính Helper đóng góp cao nhất
                val bookingHelperMap = bookings.associateBy({ it.id }, { it.helperId })
                val helperRevenueMap = mutableMapOf<Int, Double>()
                for (payment in payments) {
                    val bId = payment.bookingId ?: continue
                    val hId = bookingHelperMap[bId] ?: continue
                    helperRevenueMap[hId] = helperRevenueMap.getOrDefault(hId, 0.0) + payment.amount
                }

                val topHelperEntry = helperRevenueMap.maxByOrNull { it.value }
                if (topHelperEntry != null) {
                    val helperObj = helpers.find { it.id == topHelperEntry.key }
                    _topRevenueHelperName.postValue(helperObj?.fullname ?: "Không rõ")
                    _topRevenueValue.postValue(topHelperEntry.value)
                } else {
                    _topRevenueHelperName.postValue("Chưa có dữ liệu")
                    _topRevenueValue.postValue(0.0)
                }

                // Vẽ biểu đồ 7 ngày
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val displayFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
                val last7DaysMap = LinkedHashMap<String, Float>()
                val cal = Calendar.getInstance()

                cal.add(Calendar.DAY_OF_YEAR, -6)
                val startOf7Days = cal.apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis

                val tempCal = Calendar.getInstance()
                tempCal.timeInMillis = startOf7Days
                for (i in 0..6) {
                    last7DaysMap[dateFormat.format(tempCal.time)] = 0f
                    tempCal.add(Calendar.DAY_OF_YEAR, 1)
                }

                for (payment in payments) {
                    val dateString = payment.createdAt?.substringBefore("T") ?: continue
                    if (last7DaysMap.containsKey(dateString)) {
                        last7DaysMap[dateString] = (last7DaysMap[dateString] ?: 0f) + payment.amount.toFloat()
                    }
                }

                val chartData = last7DaysMap.map { (dateStr, amount) ->
                    val date = dateFormat.parse(dateStr)
                    val formattedDate = if (date != null) displayFormat.format(date) else dateStr
                    Pair(formattedDate, amount)
                }
                _weeklyRevenue.postValue(chartData)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // fetch tab 3
    fun fetchUXData() {
        viewModelScope.launch {
            try {
                val users = repository.getUsers()
                val helpers = users.filter { it.role.uppercase() == "HELPER" }

                val validRatings = users.mapNotNull { it.rating }.filter { it > 0.0 }
                val avgRating = if (validRatings.isNotEmpty()) validRatings.average() else 0.0
                _appAverageRating.postValue(avgRating)

                val helpersWithRating = helpers.filter { it.rating != null && it.rating > 0.0 }
                val maxUxHelper = helpersWithRating.maxByOrNull { it.rating!! }
                val minUxHelper = helpersWithRating.minByOrNull { it.rating!! }

                _topUxHelperName.postValue(maxUxHelper?.fullname ?: "Chưa có")
                _topUxHelperRating.postValue(maxUxHelper?.rating ?: 0.0)

                _bottomUxHelperName.postValue(minUxHelper?.fullname ?: "Chưa có")
                _bottomUxHelperRating.postValue(minUxHelper?.rating ?: 0.0)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}