package com.example.hcmus_quickhelper.features.booking.datasource

import com.example.hcmus_quickhelper.core.database.SupabaseClient
import com.example.hcmus_quickhelper.core.model.Booking
import com.example.hcmus_quickhelper.features.booking.model.BookingConversation
import com.example.hcmus_quickhelper.features.booking.model.BookingEvidence
import com.example.hcmus_quickhelper.features.booking.model.BookingInsert
import com.example.hcmus_quickhelper.features.booking.model.ConversationInsert
import com.example.hcmus_quickhelper.features.payment.model.Payment
import com.example.hcmus_quickhelper.features.service_browsing.model.HelperWithServicesDto
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.query.filter.FilterOperator

class BookingDataSource {
    suspend fun getAll(): List<Booking> {
        return SupabaseClient.client.from("bookings").select().decodeList<Booking>()
    }

    // Query helper, customer, service data
    suspend fun getAllFullData(): List<Booking> {
        return SupabaseClient.client.from("bookings").select(
            columns = Columns.raw("""
            *,
            customer:customer_id(*),
            helper:helper_id(*),
            services(*)
        """.trimIndent())
        ).decodeList<Booking>()
    }

    suspend fun getAllByHelperIdFullData(helperId: Int): List<Booking> {
        return SupabaseClient.client.from("bookings").select(
            columns = Columns.raw("""
            *,
            customer:customer_id(*),
            helper:helper_id(*),
            services(*)
        """.trimIndent())
        ) {
            filter {
                or {
                    eq("helper_id", helperId)
                    filter("helper_id",FilterOperator.IS, "null")
                }
            }
            order("created_at", Order.DESCENDING)
        }.decodeList<Booking>()
    }
    suspend fun getById(id: Int): Booking {
        return SupabaseClient.client.from("bookings")
            .select {
                filter {
                    eq("id", id)
                }
            }
            .decodeSingle<Booking>()
    }

    suspend fun getByIdFullData(id: Int): Booking {
        return SupabaseClient.client.from("bookings").select(
            columns = Columns.raw("""
            *,
            customer:customer_id(*),
            helper:helper_id(*),
            services(*)
        """.trimIndent())
        ) {
            filter {
                eq("id", id)
            }
        }.decodeSingle<Booking>()
    }

    suspend fun update(id: Int, booking: BookingInsert) {
        SupabaseClient.client.from("bookings").update(booking) {
            filter {
                eq("id", id)
            }
        }
    }

    suspend fun insertBooking(booking: BookingInsert) {
        SupabaseClient.client.from("bookings").insert(booking)
    }

    suspend fun insertBookingAndGet(booking: BookingInsert): Booking {
        return SupabaseClient.client.from("bookings")
            .insert(booking) { select() }
            .decodeSingle<Booking>()
    }

    suspend fun createConversation(bookingId: Int) {
        val conversationInsert = ConversationInsert(bookingId = bookingId)
        SupabaseClient.client.from("booking_conversations").insert(conversationInsert)
    }

    suspend fun getHelperWithServices(helperId: Int): HelperWithServicesDto {
        val columns = Columns.raw("id, fullname, avatar_url, rating, services(id, name, base_price)")
        return SupabaseClient.client.from("users")
            .select(columns = columns) {
                filter { eq("id", helperId) }
            }.decodeSingle<HelperWithServicesDto>()
    }

    suspend fun deleteConversationByBookingId(bookingId: Int) {
        SupabaseClient.client.from("booking_conversations").delete {
            filter {
                eq("booking_id", bookingId)
            }
        }
    }
    suspend fun getEvidences(bookingId: Int): List<BookingEvidence> {
        return SupabaseClient.client.from("booking_evidences")
            .select { filter { eq("booking_id", bookingId) } }
            .decodeList<BookingEvidence>()
    }

    suspend fun getConversationByBookingId(bookingId: Int): BookingConversation? {
        return try {
            SupabaseClient.client.from("booking_conversations")
                .select { filter { eq("booking_id", bookingId) } }
                .decodeSingleOrNull<BookingConversation>()
        } catch (e: Exception) { null }
    }

    // search booking history by id
    suspend fun getBookingsByCustomerIdFullData(customerId: Int): List<Booking> {
        return SupabaseClient.client.from("bookings").select(
            columns = Columns.raw("""
            *,
            customer:customer_id(*),
            helper:helper_id(*),
            services(*)
        """.trimIndent())
        ) {
            filter {
                eq("customer_id", customerId)
            }
            order("created_at", Order.DESCENDING)
        }.decodeList<Booking>()
    }

    suspend fun createEvidences(evidences: List<BookingEvidence>) {
        SupabaseClient.client.from("booking_evidences").insert(evidences)
    }

    suspend fun deleteEvidence(evidence: BookingEvidence) {
        SupabaseClient.client.from("booking_evidences").delete {
            filter {
                eq("booking_id", evidence.bookingId)
                eq("evidence_url", evidence.evidenceUrl)
            }
        }
    }

    suspend fun getPayment(bookingId: Int): Payment? {
        return SupabaseClient.client.from("payments")
            .select { filter { eq("booking_id", bookingId) } }
            .decodeSingleOrNull<Payment>()
    }
}