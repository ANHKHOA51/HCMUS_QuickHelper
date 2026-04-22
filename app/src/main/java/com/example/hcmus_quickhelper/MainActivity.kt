package com.example.hcmus_quickhelper

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.d("NOTI", "Permission granted")
                getFCMToken()
            } else {
                Log.d("NOTI", "Permission denied")
                // Có thể show dialog giải thích lại
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.main_activity)

//        hideNavigationBar()

        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                Log.d("FCM_TOKEN", token)
            }

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)


        bottomNav.setupWithNavController(navController)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    navController.navigate(R.id.home_fragment)
                    true
                }
                R.id.nav_service -> {
                    navController.navigate(R.id.service_list_fragment)
                    true
                }
                R.id.nav_chat -> {
                    navController.navigate(R.id.chat_list_fragment)
                    true
                }
                R.id.nav_history -> {
                    navController.navigate(R.id.booking_history_fragment)
                    true
                }
                R.id.nav_community -> {
                    navController.navigate(R.id.community_fragment)
                    true
                }
                else -> false
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.login_fragment,
                R.id.chat_fragment,
                R.id.register_fragment,
                R.id.payment_fragment,
                R.id.rating_fragment,
                R.id.receipt_fragment,
                R.id.voucher_fragment,
                R.id.booking_fragment,
                R.id.booking_request_detail_fragment,
                R.id.booking_process_helper_fragment,
                R.id.dashboard_helper_fragment,
                R.id.otp_fragment -> {
                    bottomNav.visibility = View.GONE
                }
            else -> {
                bottomNav.visibility = View.VISIBLE
            }
        }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                        == PackageManager.PERMISSION_GRANTED -> {
                    // Đã có quyền
                    getFCMToken()
                }

                shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS) -> {
                    // Giải thích trước khi xin
                    showPermissionDialog()
                }

                else -> {
                    // Xin quyền trực tiếp
                    requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // Android < 13
            getFCMToken()
        }
    }

    private fun showPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle("Cho phép thông báo")
            .setMessage("App cần quyền để gửi tin nhắn realtime cho bạn")
            .setPositiveButton("Cho phép") { _, _ ->
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
            .setNegativeButton("Không") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) return@addOnCompleteListener

                val token = task.result
                Log.d("FCM_TOKEN", token)

                // TODO: gửi token lên server của bạn
                // sendTokenToServer(token)
            }
    }

//    override fun onWindowFocusChanged(hasFocus: Boolean) {
//        super.onWindowFocusChanged(hasFocus)
//
//        // Giữ trạng thái ẩn khi quay lại app
//        if (hasFocus) {
//            hideNavigationBar()
//        }
//    }

//
}