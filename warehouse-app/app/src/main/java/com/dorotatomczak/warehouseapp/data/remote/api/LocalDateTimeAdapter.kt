package com.dorotatomczak.warehouseapp.data.remote.api

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateTimeAdapter {

    @ToJson
    fun toJson(value: LocalDateTime): String = FORMATTER.format(value)

    @FromJson
    fun fromJson(value: String): LocalDateTime = LocalDateTime.parse(value, FORMATTER)

    companion object {
        private val FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    }
}
