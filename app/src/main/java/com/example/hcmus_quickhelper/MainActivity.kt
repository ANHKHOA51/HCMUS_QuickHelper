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

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        testPaymentFragment()
//        testSupabaseConnection()
    }

    private fun testPaymentFragment() {
        val paymentFragment = PaymentFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, paymentFragment)
            .commit()
    }
    private fun testSupabaseConnection() {
        lifecycleScope.launch {
            try {
                val data = SupabaseClient.client
                    .from("users")
                    .select()
                    .decodeList<User>()

                if (data.isNotEmpty()) {
                    Log.d("SUPABASE_TEST", "Thành công! User đầu tiên: ${data[0].fullname} - Email: ${data[0].email}")
                } else {
                    Log.d("SUPABASE_TEST", "Kết nối OK nhưng bảng 'users' hiện đang trống.")
                }
            } catch (e: Exception) {
                Log.e("SUPABASE_TEST", "Lỗi: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}