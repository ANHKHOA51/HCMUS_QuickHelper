package com.example.hcmus_quickhelper.features.service_browsing.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.features.service_browsing.datasource.HomeLocalDataSource
import com.example.hcmus_quickhelper.features.service_browsing.repository.HomeRepository
import com.example.hcmus_quickhelper.features.service_browsing.viewmodel.HomeViewModel

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel

    private lateinit var tvGreeting: TextView
    private lateinit var cvProfile: CardView
    private lateinit var rvVouchers: RecyclerView
    private lateinit var rvTopHelpers: RecyclerView

    private val voucherAdapter = VoucherAdapter()
    private val topHelperAdapter = TopHelperAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.home_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDependencies()
        initViews(view)
        setupRecyclerViews()
        observeViewModel()

        viewModel.loadHomeData()
    }

    private fun setupDependencies() {
        val dataSource = HomeLocalDataSource()
        val repository = HomeRepository(dataSource)
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
    }

    private fun initViews(view: View) {
        tvGreeting = view.findViewById(R.id.tvGreeting)
        cvProfile = view.findViewById(R.id.cvProfile)
        rvVouchers = view.findViewById(R.id.rvVouchers)
        rvTopHelpers = view.findViewById(R.id.rvTopHelpers)

        cvProfile.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_profile)
        }
    }

    private fun setupRecyclerViews() {
        rvVouchers.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvVouchers.adapter = voucherAdapter

        rvTopHelpers.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvTopHelpers.adapter = topHelperAdapter
    }

    private fun observeViewModel() {
        viewModel.userProfile.observe(viewLifecycleOwner) { user ->
            tvGreeting.text = "Chào ${user?.fullname}"
        }

        viewModel.vouchers.observe(viewLifecycleOwner) { vouchers ->
            voucherAdapter.updateData(vouchers)
        }

        viewModel.topHelpers.observe(viewLifecycleOwner) { helpers ->
            topHelperAdapter.updateData(helpers)
        }
    }
}
