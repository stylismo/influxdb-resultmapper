package org.mybop.influxbd.resultmapper.converter.basics.field

import org.mybop.influxbd.resultmapper.converter.NumberFieldConverter
import kotlin.reflect.KType
import kotlin.reflect.full.createType

class DoubleConverter : NumberFieldConverter<Double?> {

    override fun supportedType() = Double::class.createType(nullable = true)

    override fun convert(value: Double?) = value

    override fun reverse(value: Double?, type: KType) = value
}
