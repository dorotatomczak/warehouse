package com.dorotatomczak.warehouseapp.data.queue

import com.dorotatomczak.warehouseapp.data.local.entity.Operation
import com.dorotatomczak.warehouseapp.data.model.Product

data class OfflineRequest (
    var id: Int? = null,
    var operation: Operation,
    var product: Product,
    var delta: Int? = null,
    var warehouseId: Int? = null
)
