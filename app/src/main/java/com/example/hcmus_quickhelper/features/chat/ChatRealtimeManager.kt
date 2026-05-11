package com.example.hcmus_quickhelper.features.chat

import android.util.Log
import com.example.hcmus_quickhelper.core.database.SupabaseClient
import com.example.hcmus_quickhelper.features.chat.model.Message
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.decodeRecord
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

object ChatRealtimeManager {

    private val _messages =
        MutableSharedFlow<Message>(
            replay = 1,
            extraBufferCapacity = 100
        )

    val messages = _messages.asSharedFlow()

    private var channel: RealtimeChannel? = null

    private var isConnected = false

    suspend fun connect() {

        if (isConnected) return

        channel = SupabaseClient.client.realtime
            .channel("global-chat")

        val flow =
            channel!!.postgresChangeFlow<PostgresAction.Insert>(
                schema = "public"
            ) {

                table = "chat_messages"
            }

        channel!!.subscribe(
            blockUntilSubscribed = true
        )

        isConnected = true

        CoroutineScope(Dispatchers.IO).launch {

            flow.collect { change ->

                val message =
                    change.decodeRecord<Message>()

                Log.d("REALTIME", message.toString())

                _messages.emit(message)
            }
        }
    }
}