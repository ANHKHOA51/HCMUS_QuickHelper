package com.example.hcmus_quickhelper.features.service_browsing.ui
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.features.service_browsing.datasource.ServiceListLocalDataSource
import com.example.hcmus_quickhelper.features.service_browsing.repository.ServiceListRepository
import com.example.hcmus_quickhelper.features.service_browsing.viewmodel.ServiceListViewModel

class ServiceListActivity : AppCompatActivity() {

    private lateinit var viewModel: ServiceListViewModel
    private lateinit var adapter: ServiceListHelperAdapter

    private lateinit var rvHelpers: RecyclerView
    private lateinit var tvResultCount: TextView
    private lateinit var btnBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.service_list_activity)

        setupDependencies()
        initViews()
        setupRecyclerView()
        observeViewModel()

        // load mock data
        viewModel.loadHelpers()
    }

    private fun setupDependencies() {
        // Manual Dependency Injection (Nếu có Hilt/Koin thì bỏ phần này)
        val localDataSource = ServiceListLocalDataSource()
        val repository = ServiceListRepository(localDataSource)

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ServiceListViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[ServiceListViewModel::class.java]
    }

    private fun initViews() {
        rvHelpers = findViewById(R.id.rvExperts)
        tvResultCount = findViewById(R.id.tvResultCount)
        btnBack = findViewById(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter =  ServiceListHelperAdapter { helper ->
            // Xử lý sự kiện khi bấm nút "Đặt"
            Toast.makeText(this, "Đang đặt lịch với ${helper.name}", Toast.LENGTH_SHORT).show()
        }
        rvHelpers.layoutManager = LinearLayoutManager(this)
        rvHelpers.adapter = adapter
    }

    private fun observeViewModel() {
        // Observe danh sách chuyên gia
        viewModel.helpers.observe(this) { helpers ->
            adapter.updateData(helpers)
            tvResultCount.text = "Tìm thấy ${helpers.size} chuyên gia phù hợp"
        }

        // Observe loading
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                // tvResultCount.text = "Đang tìm kiếm..."
            }
        }
    }
}