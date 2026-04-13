package com.example.hcmus_quickhelper.features.service_browsing.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.features.service_browsing.datasource.ServiceListLocalDataSource
import com.example.hcmus_quickhelper.features.service_browsing.repository.ServiceListRepository
import com.example.hcmus_quickhelper.features.service_browsing.viewmodel.ServiceListViewModel

class ServiceListFragment : Fragment() {
    private lateinit var viewModel: ServiceListViewModel
    private lateinit var adapter: ServiceListHelperAdapter
    private lateinit var rvHelpers: RecyclerView
    private lateinit var tvResultCount: TextView
    private lateinit var btnBack: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.service_list_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDependencies()
        initViews(view)
        setupRecyclerView()
        observeViewModel()

        // load mock data
        viewModel.loadHelpers()
    }

    private fun setupDependencies() {
        val localDataSource = ServiceListLocalDataSource()
        val repository = ServiceListRepository(localDataSource)

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ServiceListViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[ServiceListViewModel::class.java]
    }

    private fun initViews(view: View) {
        rvHelpers = view.findViewById(R.id.rvExperts)
        tvResultCount = view.findViewById(R.id.tvResultCount)
        btnBack = view.findViewById(R.id.btnBack)

        btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        adapter = ServiceListHelperAdapter { helper ->
            findNavController().navigate(R.id.action_service_to_booking)
        }
        rvHelpers.layoutManager = LinearLayoutManager(requireContext())
        rvHelpers.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.helpers.observe(viewLifecycleOwner) { helpers ->
            adapter.updateData(helpers)
            tvResultCount.text = "Tìm thấy ${helpers.size} chuyên gia phù hợp"
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                // tvResultCount.text = "Đang tìm kiếm..."
            }
        }
    }
}