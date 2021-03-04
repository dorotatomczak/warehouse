package com.dorotatomczak.warehouseapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import com.dorotatomczak.warehouseapp.data.model.ProductInWarehouse

@Entity(
    tableName = "productsWithWarehouses",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = CASCADE
        )]
)
data class ProductWithWarehouseEntity(
    @PrimaryKey(autoGenerate = true) var productsWithWarehousesId: Int? = null,
    @ColumnInfo(name = "quantity") var quantity: Int = 0,
    var productId: Int? = null,
    var warehouseId: Int? = null
) {
    companion object {
        fun from(productInWarehouse: ProductInWarehouse): ProductWithWarehouseEntity {
            return ProductWithWarehouseEntity(
                productId = productInWarehouse.productId,
                quantity = productInWarehouse.quantity,
                warehouseId = productInWarehouse.warehouseId
            )
        }
    }
}
