package com.example.hcmus_quickhelper

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.hcmus_quickhelper.core.database.SupabaseClient // Giả sử bạn đặt tên object là SupabaseClient
import com.example.hcmus_quickhelper.core.model.User
import com.example.hcmus_quickhelper.features.payment.ui.PaymentFragment
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

// testing
import android.content.Intent
import com.example.hcmus_quickhelper.features.service_browsing.ui.ServiceListActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        val intent = Intent(this, ServiceListActivity::class.java)
        startActivity(intent)
    }
}