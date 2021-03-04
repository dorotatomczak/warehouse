package com.dorotatomczak.warehouseapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dorotatomczak.warehouseapp.data.model.Warehouse

@Entity(tableName = "warehouses")
data class WarehouseEntity(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    @ColumnInfo(name = "name") var name: String = "",
) {
    companion object {
        fun from(warehouse: Warehouse): WarehouseEntity {
            return WarehouseEntity(
                warehouse.id,
                warehouse.name
            )
        }
    }

    fun toWarehouse(): Warehouse {
        return Warehouse(id, name)
    }
}
