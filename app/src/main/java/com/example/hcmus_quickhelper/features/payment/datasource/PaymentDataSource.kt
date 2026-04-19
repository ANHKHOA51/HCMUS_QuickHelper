package com.example.hcmus_quickhelper.features.payment.datasource

import com.example.hcmus_quickhelper.core.database.SupabaseClient
import com.example.hcmus_quickhelper.features.payment.model.Payment
import com.example.hcmus_quickhelper.features.payment.model.PaymentInsert
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

class PaymentDataSource {
    suspend fun getById(id: Int): Payment? {
        return SupabaseClient.client.from("payments")
            .select {
                filter { eq("id", id) }
            }
            .decodeSingleOrNull<Payment>()
    }

    suspend fun getByIdFullData(paymentId: Int): Payment {
        return SupabaseClient.client
            .from("payments")
            .select(columns = Columns.raw("""
                *,
                booking:bookings (
                    *,
                    customer:users!bookings_customer_id_fkey (*),
                    helper:users!bookings_helper_id_fkey (*),
                    services:services (*)
                ),
                voucher:vouchers (*)
            """.trimIndent())
            ) {
                filter {
                    eq("id", paymentId)
                }
            }.decodeSingle<Payment>()
    }

    suspend fun getAll(): List<Payment> {
        return SupabaseClient.client.from("payments").select().decodeList<Payment>()
    }

    suspend fun getAllFullData(): List<Payment> {
        return SupabaseClient.client
            .from("payments")
            .select(columns = Columns.raw("""
                *,
                booking:bookings (
                    *,
                    customer:users!bookings_customer_id_fkey (*),
                    helper:users!bookings_helper_id_fkey (*),
                    services:services (*)
                ),
                voucher:vouchers (*)
            """.trimIndent()))
            .decodeList<Payment>()
    }

    suspend fun getByBookingId(id: Int): Payment? {
        return SupabaseClient.client.from("payments")
            .select{
                filter { eq("booking_id", id) }
            }
            .decodeSingleOrNull<Payment>()
    }

    suspend fun getByBookingIdFullData(bookingId: Int): Payment {
        return SupabaseClient.client
            .from("payments")
            .select(columns = Columns.raw("""
                *,
                booking:bookings (
                    *,
                    customer:users!bookings_customer_id_fkey (*),
                    helper:users!bookings_helper_id_fkey (*),
                    services:services (*)
                ),
                voucher:vouchers (*)
            """.trimIndent())
            ) {
                filter {
                    eq("booking_id", bookingId)
                }
            }.decodeSingle<Payment>()
    }

    suspend fun insert(payment: PaymentInsert): Payment {
        return SupabaseClient.client.from("payments").insert(payment) {
            select()
        }.decodeSingle<Payment>()
    }

    suspend fun update(id: Int, payment: PaymentInsert): Payment {
        return SupabaseClient.client.from("payments").update(payment){
            select()
            filter {
                eq("id", id)
            }
        }.decodeSingle<Payment>()
    }
}