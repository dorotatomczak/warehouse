package com.dorotatomczak.warehouseapp.data.model

import com.dorotatomczak.warehouseapp.data.queue.OfflineRequest

data class SyncResult(
    val request: OfflineRequest,
    val wasSuccessful: Boolean,
    val responseMessage: String = ""
)
