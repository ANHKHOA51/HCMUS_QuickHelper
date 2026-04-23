package com.example.hcmus_quickhelper

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.main_activity)

        hideNavigationBar()

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
                R.id.OTPFragment -> {
                    bottomNav.visibility = View.GONE
                }
            else -> {
                bottomNav.visibility = View.VISIBLE
            }
        }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        // Giữ trạng thái ẩn khi quay lại app
        if (hasFocus) {
            hideNavigationBar()
        }
    }

    private fun hideNavigationBar() {
        val controller = WindowInsetsControllerCompat(window, window.decorView)

        // ❗ CHỈ ẩn navigation bar
        controller.hide(WindowInsetsCompat.Type.navigationBars())

        // Cho phép vuốt để hiện lại tạm thời
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}