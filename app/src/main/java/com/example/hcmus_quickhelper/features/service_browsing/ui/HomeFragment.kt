package com.example.hcmus_quickhelper.features.service_browsing.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.features.service_browsing.datasource.HomeRemoteDataSource
import com.example.hcmus_quickhelper.features.service_browsing.repository.HomeRepository
import com.example.hcmus_quickhelper.features.service_browsing.viewmodel.HomeViewModel

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel

    private lateinit var tvGreeting: TextView
    private lateinit var cvProfile: CardView
    private lateinit var etSearch: EditText
    private lateinit var tvViewAllServices: TextView
    private lateinit var tvViewAllTopHelpers: TextView
    private lateinit var rvVouchers: RecyclerView
    private lateinit var rvTopHelpers: RecyclerView
    private lateinit var rvPopularServices: RecyclerView

    private val voucherAdapter = VoucherAdapter()

    private lateinit var topHelperAdapter: TopHelperAdapter
    private lateinit var serviceAdapter: ServiceAdapter

    private lateinit var tvViewAllVoucher: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.home_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDependencies()
        initViews(view)
        setupRecyclerViews()
        setupInteractions()
        observeViewModel()

        viewModel.loadHomeData()
    }

    private fun setupDependencies() {
        val dataSource = HomeRemoteDataSource()
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
        etSearch = view.findViewById(R.id.etSearch)
        cvProfile = view.findViewById(R.id.cvProfile)
        rvVouchers = view.findViewById(R.id.rvVouchers)
        rvTopHelpers = view.findViewById(R.id.rvTopHelpers)
        rvPopularServices = view.findViewById(R.id.rvPopularServices)

        tvViewAllServices = view.findViewById(R.id.tvViewAllServices)
        tvViewAllTopHelpers = view.findViewById(R.id.tvViewAllTopHelpers)

        cvProfile.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_settings)
        }
    }

    private fun setupRecyclerViews() {
        topHelperAdapter = TopHelperAdapter { helper ->
            navigateToServiceList(helper.name)
        }

        serviceAdapter = ServiceAdapter { service ->
            navigateToServiceList(service.name)
        }

        rvVouchers.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvVouchers.adapter = voucherAdapter

        rvTopHelpers.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvTopHelpers.adapter = topHelperAdapter

        rvPopularServices.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvPopularServices.adapter = serviceAdapter

        tvViewAllVoucher = view?.findViewById(R.id.tvViewAllVoucher)!!
    }

    private fun setupInteractions() {
        etSearch.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = v.text.toString().trim()
                if (query.isNotEmpty()) {
                    navigateToServiceList(query)
                }
                true
            } else {
                false
            }
        }

        val viewAllClickListener = View.OnClickListener {
            navigateToServiceList("")
        }

        tvViewAllServices.setOnClickListener(viewAllClickListener)
        tvViewAllTopHelpers.setOnClickListener(viewAllClickListener)
        tvViewAllVoucher.setOnClickListener { findNavController().navigate(R.id.action_home_fragment_to_collect_voucher_fragment) }
    }

    private fun navigateToServiceList(query: String) {
        if (findNavController().currentDestination?.id == R.id.home_fragment) {
            val bundle = Bundle().apply {
                putString("searchQuery", query)
            }
            findNavController().navigate(R.id.action_home_to_services, bundle)
        }
    }

    private fun observeViewModel() {
        viewModel.userProfile.observe(viewLifecycleOwner) { user ->
            tvGreeting.text = getString(R.string.greeting_user, user?.fullname ?: "")
        }

        viewModel.vouchers.observe(viewLifecycleOwner) { vouchers ->
            voucherAdapter.updateData(vouchers)
        }

        viewModel.topHelpers.observe(viewLifecycleOwner) { helpers ->
            topHelperAdapter.updateData(helpers)
        }

        viewModel.services.observe(viewLifecycleOwner) { services ->
            serviceAdapter.updateData(services)
        }
    }
}
