package com.example.hcmus_quickhelper.features.payment.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.databinding.FragmentPaymentAdminBinding
import com.example.hcmus_quickhelper.features.payment.datasource.PaymentDataSource
import com.example.hcmus_quickhelper.features.payment.repository.PaymentRepository
import com.example.hcmus_quickhelper.features.payment.viewmodel.PaymentAdminViewModel

class PaymentAdminFragment : Fragment() {

    private var _binding: FragmentPaymentAdminBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PaymentAdminViewModel
    private val paymentAdapter by lazy {
        PaymentAdminAdapter { payment ->
            val bundle = Bundle().apply {
                putInt("paymentId", payment.id!!)
            }
//            findNavController().navigate(
//                R.id.action_payment_admin_fragment_to_payment_detail_admin_fragment,
//                bundle
//            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        setupObserve()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.loadPayments()
    }

    private fun setupViewModel() {
        val repository = PaymentRepository(PaymentDataSource())
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return PaymentAdminViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[PaymentAdminViewModel::class.java]
    }

    private fun setupRecyclerView() {
        binding.rvPayments.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = paymentAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupObserve() {
        viewModel.payments.observe(viewLifecycleOwner) { list ->
            paymentAdapter.updateData(list)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
