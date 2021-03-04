package com.dorotatomczak.warehouseapp.ui.util

import androidx.databinding.InverseMethod

object Converter {

    @InverseMethod("stringToFloat")
    @JvmStatic
    fun floatToString(value: Float): String = value.toString()

    @JvmStatic
    fun stringToFloat(value: String): Float {
        if (value.isBlank()) {
            return 0f
        }
        return value.toFloat()
    }

    fun stringToInt(value: String): Int {
        if (value.isBlank()) {
            return 0
        }
        return value.toInt()
    }

}
