package com.dorotatomczak.warehouseapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dorotatomczak.warehouseapp.data.model.Product
import com.dorotatomczak.warehouseapp.data.queue.OfflineRequest

@Entity(tableName = "requests")
class RequestEntity(
    @PrimaryKey(autoGenerate = true) var requestId: Int? = null,
    @ColumnInfo(name = "operation") var operation: Operation,
    @Embedded var product: Product,
    @ColumnInfo(name = "delta") var delta: Int? = null,
    @ColumnInfo(name = "warehouse_id") var warehouseId: Int? = null
) {
    fun toOfflineRequest(): OfflineRequest {
        return OfflineRequest(requestId, operation, product, delta, warehouseId)
    }
}
