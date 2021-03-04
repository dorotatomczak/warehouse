package com.dorotatomczak.warehouseapp.data.local.dao

import androidx.room.*
import com.dorotatomczak.warehouseapp.data.local.entity.ProductEntity
import com.dorotatomczak.warehouseapp.data.local.entity.ProductWithWarehouseEntity
import com.dorotatomczak.warehouseapp.data.local.entity.WarehouseEntity
import com.dorotatomczak.warehouseapp.data.model.Product
import com.dorotatomczak.warehouseapp.data.model.ProductInWarehouse


@Dao
interface ProductDao {

    @Query("SELECT * FROM products")
    suspend fun getAll(): List<ProductEntity>

    @Query("SELECT * FROM products WHERE id LIKE :id")
    fun findById(id: Int): ProductEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: ProductEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAll(products: List<ProductEntity>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(product: ProductEntity)

    @Delete
    suspend fun delete(product: ProductEntity)

    @Query("DELETE FROM products")
    suspend fun deleteAll()

    @Query("DELETE FROM productsWithWarehouses")
    suspend fun deleteAllProductsWithWarehouses()

    @Query("SELECT products.is_online AS isOnline, * FROM products INNER JOIN productsWithWarehouses ON productsWithWarehouses.productId = products.id WHERE productsWithWarehouses.warehouseId LIKE :warehouseId")
    suspend fun getAllProductsFromWarehouse(warehouseId: Int): List<Product>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProductsWithWarehouses(productWithWarehouseEntity: List<ProductWithWarehouseEntity>)

    @Query("UPDATE productsWithWarehouses SET quantity = :quantity WHERE productId LIKE :productId AND warehouseId LIKE :warehouseId")
    suspend fun updateProductQuantity(productId: Int, warehouseId: Int, quantity: Int)

    @Transaction
    suspend fun insertProductsWithWarehouse(
        products: List<Product>,
        productsInWarehouses: List<ProductInWarehouse>
    ) {
        deleteAll()
        insertAll(products.map { ProductEntity.from(it) })
        insertAllProductsWithWarehouses(productsInWarehouses.map {
            ProductWithWarehouseEntity.from(
                it
            )
        })
    }

    @Transaction
    suspend fun insertProductWithWarehouses(
        product: Product,
        warehouses: List<WarehouseEntity>
    ): Product {
        val id = insert(ProductEntity.from(product)).toInt()

        val productInWarehouses = mutableListOf<ProductWithWarehouseEntity>()
        for (warehouse in warehouses) {
            productInWarehouses.add(
                ProductWithWarehouseEntity(
                    quantity = product.quantity,
                    productId = id,
                    warehouseId = warehouse.id
                )
            )
        }

        insertAllProductsWithWarehouses(productInWarehouses)
        return findById(id).toProduct(product.quantity)
    }

    @Transaction
    suspend fun changeProductQuantity(product: Product, warehouseId: Int): Product {
        updateProductQuantity(product.id!!, warehouseId, product.quantity)
        return findById(product.id!!).toProduct(product.quantity)
    }
}
