package com.example.hcmus_quickhelper.features.voucher.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.features.booking.viewmodel.BookingTab
import com.example.hcmus_quickhelper.features.voucher.model.Voucher
import com.example.hcmus_quickhelper.features.voucher.repository.VoucherRepository
import kotlinx.coroutines.launch

enum class CollectVoucherTab {
    COLLECT, STORAGE
}

class CollectVoucherViewModel (
    private val voucherRepository: VoucherRepository
) : ViewModel() {
    private val _collectibleVouchers = MutableLiveData<List<Voucher>>()
    val collectibleVouchers: LiveData<List<Voucher>> = _collectibleVouchers

    private  val _myVouchers = MutableLiveData<List<Voucher>>()
    val myVouchers: LiveData<List<Voucher>> = _myVouchers

    private val _vouchers = MutableLiveData<List<Voucher>>() // The voucher is used for display.
    val vouchers: LiveData<List<Voucher>> = _vouchers

    private val _currentTab = MutableLiveData<CollectVoucherTab>()
    val currentTab: LiveData<CollectVoucherTab> = _currentTab

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadData(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val collectibleVouchersData = voucherRepository.getAllVoucherCollectible(userId)
                _collectibleVouchers.value = collectibleVouchersData

                val myVouchersData = voucherRepository.getVouchersByUserId(userId)
                _myVouchers.value = myVouchersData

                _currentTab.value = CollectVoucherTab.COLLECT
                setVouchers()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMyVouchers(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val myVouchersData = voucherRepository.getVouchersByUserId(userId)
                _myVouchers.value = myVouchersData

                setVouchers()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            finally {
                _isLoading.value = false
            }
        }
    }

    fun loadCollectibleVouchers(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val collectibleVouchersData = voucherRepository.getAllVoucherCollectible(userId)
                _collectibleVouchers.value = collectibleVouchersData

                setVouchers()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            finally {
                _isLoading.value = false
            }
        }
    }

    fun collectVoucher(voucherId: Int, userId: Int) {
        viewModelScope.launch {
            try {

                val response = voucherRepository.collectVoucher(voucherId, userId)
                if(response.success) {
                    Log.d("TEST", "SUCCESS")
                }
                else {
                    // Notify failed
                    Log.d("TEST", "FAILED")
                }
            } catch (err: Exception) {
                Log.d("ERROR", err.toString())
            }
        }
    }

    private fun setVouchers() {
        val tab = _currentTab.value ?: CollectVoucherTab.COLLECT
        val filteredList = when (tab) {
            CollectVoucherTab.COLLECT -> _collectibleVouchers.value
            CollectVoucherTab.STORAGE -> _myVouchers.value
        }
        _vouchers.value = filteredList!!
    }

    fun selectTab(tab: CollectVoucherTab) {
        _currentTab.value = tab
        setVouchers()
    }
}