package org.mybop.influxbd.resultmapper.converter.basics.time

import org.mybop.influxbd.resultmapper.converter.TimeConverter
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import kotlin.reflect.full.createType

internal class ZonedDateTimeConverter : TimeConverter<ZonedDateTime> {

    override fun supportedType() = ZonedDateTime::class.createType()

    override fun convert(instant: ZonedDateTime): Long {
        val javaInstant = instant.toInstant()
        return javaInstant.epochSecond * 1000000000L + javaInstant.nano
    }

    override fun precision() = TimeUnit.NANOSECONDS

    override fun reverse(value: String): ZonedDateTime = ZonedDateTime.parse(value)
}
