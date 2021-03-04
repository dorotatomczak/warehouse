package com.dorotatomczak.warehouseapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dorotatomczak.warehouseapp.data.local.entity.RequestEntity

@Dao
interface RequestDao {

    @Query("SELECT * FROM requests")
    suspend fun getAll(): List<RequestEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(request: RequestEntity): Long

    @Query("DELETE FROM requests WHERE id LIKE :id ")
    suspend fun deleteByProductId(id: Int)

    @Query("DELETE FROM requests WHERE requestId LIKE :requestId")
    suspend fun deleteByRequestId(requestId: Int)

    @Query("DELETE FROM requests")
    suspend fun deleteAll()

    @Query("UPDATE requests SET name = :name, manufacturer = :manufacturer, price = :price, quantity = :quantity WHERE id LIKE :id")
    suspend fun updateByProductId(
        id: Int,
        name: String,
        manufacturer: String,
        price: Float,
        quantity: Int
    )
}
