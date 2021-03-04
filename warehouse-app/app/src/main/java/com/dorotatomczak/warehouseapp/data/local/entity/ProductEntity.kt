package com.dorotatomczak.warehouseapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dorotatomczak.warehouseapp.data.model.Product
import java.time.LocalDateTime

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    @ColumnInfo(name = "name") var name: String = "",
    @ColumnInfo(name = "manufacturer") var manufacturer: String = "",
    @ColumnInfo(name = "price") var price: Float = 0f,
    @ColumnInfo(name = "modified") val modified: LocalDateTime? = LocalDateTime.now(),
    @ColumnInfo(name = "is_online") var isOnline: Boolean = false
) {
    companion object {
        fun from(product: Product): ProductEntity {
            return ProductEntity(
                product.id,
                product.name,
                product.manufacturer,
                product.price,
                product.modified,
                product.isOnline
            )
        }
    }

    fun toProduct(quantity: Int): Product {
        return Product(id, name, manufacturer, price, quantity, modified, isOnline)
    }
}
