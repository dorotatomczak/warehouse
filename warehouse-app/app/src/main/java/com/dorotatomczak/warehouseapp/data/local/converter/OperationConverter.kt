package com.dorotatomczak.warehouseapp.data.local.converter

import androidx.room.TypeConverter
import com.dorotatomczak.warehouseapp.data.local.entity.Operation

class OperationConverter {

    @TypeConverter
    fun fromHealthToInt(value: Operation): Int = value.ordinal

    @TypeConverter
    fun fromIntToHealth(value: Int): Operation = Operation.values()[value]
}
