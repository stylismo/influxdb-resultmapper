package org.mybop.influxbd.resultmapper

import org.mybop.influxbd.resultmapper.converter.TimeConverter
import java.time.Instant
import java.util.concurrent.TimeUnit
import kotlin.reflect.full.createType

class TimestampMillisConverter : TimeConverter<Long> {

    override fun supportedType() = Long::class.createType(nullable = true)

    override fun convert(instant: Long) = instant

    override fun precision() = TimeUnit.MILLISECONDS

    override fun reverse(value: String) = Instant.parse(value).toEpochMilli()
}
