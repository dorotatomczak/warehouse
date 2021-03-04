package com.dorotatomczak.oauth.warehousebackend.repository.converter

import java.sql.Timestamp
import java.time.LocalDateTime
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter(autoApply = true)
class LocalDateTimeConverter : AttributeConverter<LocalDateTime, Timestamp> {

    override fun convertToDatabaseColumn(attribute: LocalDateTime?): Timestamp {
        return Timestamp.valueOf(attribute)
    }

    override fun convertToEntityAttribute(dbData: Timestamp?): LocalDateTime? {
        return dbData?.toLocalDateTime()
    }
}