package com.example.hcmus_quickhelper.core.service

import android.util.Log
import coil.util.Logger
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.util.UUID

data class MQTask(
    val id: String = UUID.randomUUID().toString(),
    val action: suspend () -> Unit
)

object MQService {
    private val taskChannel = Channel<MQTask>(Channel.UNLIMITED)

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        startWorker()
    }

    private fun startWorker() {
        serviceScope.launch {
            for (task in taskChannel) {
                try {
                    Log.d("MQ","MQService: Đang xử lý tác vụ ${task.id}")
                    task.action()
                    Log.d("MQ","MQService: Hoàn thành tác vụ ${task.id}")
                } catch (e: Exception) {
                    println("MQService: Lỗi khi xử lý ${task.id}: ${e.message}")
                }
            }
        }
    }

    fun postTask(action: suspend () -> Unit) {
        serviceScope.launch {
            taskChannel.send(MQTask(action = action))
        }
    }
}