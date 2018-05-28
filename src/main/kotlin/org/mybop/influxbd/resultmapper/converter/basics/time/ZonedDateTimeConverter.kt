package org.mybop.influxbd.resultmapper.converter.basics.time

import org.mybop.influxbd.resultmapper.converter.TimeConverter
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import kotlin.reflect.full.createType

class ZonedDateTimeConverter : TimeConverter<ZonedDateTime> {

    override fun supportedType() = ZonedDateTime::class.createType()

    override fun convert(instant: ZonedDateTime) = instant.toInstant().toEpochMilli()

    override fun precision() = TimeUnit.MILLISECONDS
}
