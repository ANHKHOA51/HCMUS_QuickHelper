package com.example.hcmus_quickhelper.features.admin_management.ui

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
// import androidx.lifecycle.ViewModelProvider
// import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.databinding.FragmentAdminManagementBinding
import com.example.hcmus_quickhelper.features.admin_management.datasource.ManagementDataSource
import com.example.hcmus_quickhelper.features.admin_management.repository.ManagementRepository
import com.example.hcmus_quickhelper.features.admin_management.viewmodel.ManagementViewModel
import com.example.hcmus_quickhelper.features.community.datasource.CommunityRemoteDataSource
import com.example.hcmus_quickhelper.features.community.repository.CommunityRepository
import com.example.hcmus_quickhelper.features.community.viewmodel.CommunityViewModel

class ManagementFragment : Fragment(R.layout.fragment_admin_management) {

    private lateinit var binding: FragmentAdminManagementBinding
     private lateinit var viewModel: ManagementViewModel
     private lateinit var userAdapter: UserAdminAdapter
     private lateinit var feedAdapter: FeedAdminAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAdminManagementBinding.bind(view)

        val dataSource = ManagementDataSource()
        val repository = ManagementRepository(dataSource)
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ManagementViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return ManagementViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

        viewModel = ViewModelProvider(this, factory)[ManagementViewModel::class.java]

        setupRecyclerViews()
        setupListeners()
        observeViewModel()

        // Mặc định chọn Tab Người dùng (User) khi mở màn hình
        selectTab(isUserTab = true)
    }

    private fun setupRecyclerViews() {
//         Cài đặt cho rvUsers
         userAdapter = UserAdminAdapter(emptyList())
         binding.rvUsers.apply {
             layoutManager = LinearLayoutManager(requireContext())
             adapter = userAdapter
         }

//         Cài đặt cho rvFeeds
         feedAdapter = FeedAdminAdapter(emptyList())
         binding.rvFeeds.apply {
             layoutManager = LinearLayoutManager(requireContext())
             adapter = feedAdapter
         }
    }

    private fun setupListeners() {
        // Nút Back
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Bắt sự kiện chuyển Tab
        binding.cvTabUser.setOnClickListener {
            selectTab(isUserTab = true)
        }

        binding.cvTabFeed.setOnClickListener {
            selectTab(isUserTab = false)
        }
    }

    // Logic UI chuyển Tab VÀ Tải Dữ Liệu
    private fun selectTab(isUserTab: Boolean) {
        val unselectedColor = Color.parseColor("#00000000") // Trong suốt
        val unselectedTextColor = Color.parseColor("#6C757D") // Xám
        val unselectedElevation = 0f

        val selectedColor = Color.parseColor("#FFFFFF") // Trắng
        val selectedTextColor = Color.parseColor("#E56B3D") // Cam
        val selectedElevation = 4f // Tương đương 2dp elevation

        if (isUserTab) {
            // 1. Kích hoạt Tab User
            binding.cvTabUser.setCardBackgroundColor(selectedColor)
            binding.tvTabUser.setTextColor(selectedTextColor)
            binding.cvTabUser.cardElevation = selectedElevation
            binding.tvTabUser.setTypeface(null, Typeface.BOLD)

            // 2. Vô hiệu hóa Tab Feed
            binding.cvTabFeed.setCardBackgroundColor(unselectedColor)
            binding.tvTabFeed.setTextColor(unselectedTextColor)
            binding.cvTabFeed.cardElevation = unselectedElevation
            binding.tvTabFeed.setTypeface(null, Typeface.NORMAL)

            // 3. Đổi nội dung hiển thị
            binding.llTabUser.visibility = View.VISIBLE
            binding.llTabFeed.visibility = View.GONE

            // TODO: Gọi API load danh sách Users (nếu chưa load)
             viewModel.getUsers()

        } else {
            // 1. Kích hoạt Tab Feed
            binding.cvTabFeed.setCardBackgroundColor(selectedColor)
            binding.tvTabFeed.setTextColor(selectedTextColor)
            binding.cvTabFeed.cardElevation = selectedElevation
            binding.tvTabFeed.setTypeface(null, Typeface.BOLD)

            // 2. Vô hiệu hóa Tab User
            binding.cvTabUser.setCardBackgroundColor(unselectedColor)
            binding.tvTabUser.setTextColor(unselectedTextColor)
            binding.cvTabUser.cardElevation = unselectedElevation
            binding.tvTabUser.setTypeface(null, Typeface.NORMAL)

            // 3. Đổi nội dung hiển thị
            binding.llTabUser.visibility = View.GONE
            binding.llTabFeed.visibility = View.VISIBLE

            // TODO: Gọi API load danh sách Feeds (nếu chưa load)
             viewModel.getFeeds()
        }
    }


    private fun observeViewModel() {
        viewModel.users.observe(viewLifecycleOwner) { usersList ->
            userAdapter.updateData(usersList)
        }

        viewModel.feeds.observe(viewLifecycleOwner) { feedsList ->
            feedAdapter.updateData(feedsList)
        }
    }

}