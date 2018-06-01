package org.mybop.influxbd.resultmapper.converter.basics.time

import org.mybop.influxbd.resultmapper.converter.TimeConverter
import java.time.Instant
import java.util.concurrent.TimeUnit
import kotlin.reflect.full.createType

internal class InstantConverter : TimeConverter<Instant> {

    override fun supportedType() = Instant::class.createType()

    override fun convert(instant: Instant) = instant.epochSecond * 1000000000L + instant.nano

    override fun precision() = TimeUnit.NANOSECONDS

    override fun reverse(value: String): Instant = Instant.parse(value)
}
