package com.example.hcmus_quickhelper.features.service_browsing.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.features.service_browsing.datasource.ServiceListRemoteDataSource
import com.example.hcmus_quickhelper.features.service_browsing.repository.ServiceListRepository
import com.example.hcmus_quickhelper.features.service_browsing.viewmodel.ServiceListViewModel
import  com.example.hcmus_quickhelper.databinding.ServiceListActivityBinding

class ServiceListFragment : Fragment() {
    private var _binding: ServiceListActivityBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ServiceListViewModel
    private lateinit var adapter: ServiceListHelperAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ServiceListActivityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        setupSearchAndFilters()
        observeViewModel()
        val query = arguments?.getString("searchQuery")
        if (!query.isNullOrEmpty()) {
            binding.etSearch.setText(query)
        }

        viewModel.loadHelpers()
    }

    private fun setupViewModel() {
        val dataSource = ServiceListRemoteDataSource()
        val repository = ServiceListRepository(dataSource)

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ServiceListViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return ServiceListViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
        viewModel = ViewModelProvider(this, factory)[ServiceListViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = ServiceListHelperAdapter(emptyList()) { helper ->
            val bundle = Bundle().apply {
                putInt("helperId", helper.id)
            }
            view?.findNavController()?.navigate(R.id.action_service_to_booking, bundle)
        }

        binding.rvExperts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@ServiceListFragment.adapter
            setHasFixedSize(true)
        }

        binding.btnBack.setOnClickListener {
            view?.findNavController()?.navigateUp()
        }
    }

    private fun setupSearchAndFilters() {
        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            viewModel.search(text?.toString() ?: "")
        }

        binding.chipGroupFilter.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                viewModel.setFilter(checkedIds.first())
            } else {
                group.check(R.id.chipAll)
                viewModel.setFilter(R.id.chipAll)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.helpers.observe(viewLifecycleOwner) { list ->
            adapter.updateData(list)
            binding.tvResultCount.text = getString(R.string.result_count_format, list.size)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
