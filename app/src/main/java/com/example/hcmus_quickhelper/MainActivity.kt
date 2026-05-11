package com.example.hcmus_quickhelper

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.hcmus_quickhelper.core.auth.UserPreferences
import com.example.hcmus_quickhelper.core.utils.LanguageUtils
import com.example.hcmus_quickhelper.features.chat.ChatRealtimeManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.d("NOTI", "Permission granted")
                getFCMToken()
            } else {
                Log.d("NOTI", "Permission denied")
            }
        }

    override fun attachBaseContext(newBase: Context) {
        val prefs = UserPreferences(newBase)
        val lang = prefs.getLanguageBlocking()
        super.attachBaseContext(LanguageUtils.wrapContext(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        CoroutineScope(Dispatchers.IO).launch {
            ChatRealtimeManager.connect()
        }

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
                R.id.home_fragment,
                R.id.service_list_fragment,
                R.id.chat_list_fragment,
                R.id.booking_history_fragment,
                R.id.community_fragment-> {
                    bottomNav.visibility = View.VISIBLE
                }
            else -> {
                bottomNav.visibility = View.GONE
            }
        }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                        == PackageManager.PERMISSION_GRANTED -> {
                    getFCMToken()
                }

                shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS) -> {
                    showPermissionDialog()
                }

                else -> {
                    requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
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
            }
    }
}
