package org.mybop.influxbd.resultmapper.converter.basics.field

import org.mybop.influxbd.resultmapper.converter.FloatFieldConverter
import kotlin.reflect.full.createType

class FloatConverter : FloatFieldConverter<Float?> {

    override fun supportedType() = Float::class.createType(nullable = true)

    override fun convert(value: Float?) = value

    override fun reverse(value: Float?) = value
}
