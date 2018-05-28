package org.mybop.influxbd.resultmapper.converter.basics.time

import org.mybop.influxbd.resultmapper.converter.TimeConverter
import java.time.Instant
import java.util.concurrent.TimeUnit
import kotlin.reflect.full.createType

class InstantConverter : TimeConverter<Instant> {
    override fun supportedType() = Instant::class.createType()

    override fun convert(instant: Instant) = instant.toEpochMilli()

    override fun precision() = TimeUnit.MILLISECONDS
}
