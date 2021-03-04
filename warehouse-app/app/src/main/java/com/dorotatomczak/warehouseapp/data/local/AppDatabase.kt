package com.dorotatomczak.warehouseapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dorotatomczak.warehouseapp.data.local.converter.OperationConverter
import com.dorotatomczak.warehouseapp.data.local.converter.TimeConverter
import com.dorotatomczak.warehouseapp.data.local.dao.ProductDao
import com.dorotatomczak.warehouseapp.data.local.dao.RequestDao
import com.dorotatomczak.warehouseapp.data.local.dao.WarehouseDao
import com.dorotatomczak.warehouseapp.data.local.entity.ProductEntity
import com.dorotatomczak.warehouseapp.data.local.entity.ProductWithWarehouseEntity
import com.dorotatomczak.warehouseapp.data.local.entity.RequestEntity
import com.dorotatomczak.warehouseapp.data.local.entity.WarehouseEntity

@Database(
    entities = [ProductEntity::class, RequestEntity::class, WarehouseEntity::class, ProductWithWarehouseEntity::class],
    version = 7
)
@TypeConverters(OperationConverter::class, TimeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        const val DATABASE_NAME = "warehouse_app_database"
    }

    abstract fun productDao(): ProductDao

    abstract fun requestDao(): RequestDao

    abstract fun warehouseDao(): WarehouseDao
}
