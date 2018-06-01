package org.mybop.influxbd.resultmapper.converter.basics.field

import org.mybop.influxbd.resultmapper.converter.NumberFieldConverter
import kotlin.reflect.KType
import kotlin.reflect.full.createType

internal class FloatConverter : NumberFieldConverter<Float?> {

    override fun supportedType() = Float::class.createType(nullable = true)

    override fun convert(value: Float?) = value?.toDouble()

    override fun reverse(value: Double?, type: KType) = value?.toFloat()
}
