package org.mybop.influxbd.resultmapper.converter.basics.field

import org.mybop.influxbd.resultmapper.converter.DoubleFieldConverter
import kotlin.reflect.full.createType

class DoubleConverter : DoubleFieldConverter<Double?> {

    override fun supportedType() = Double::class.createType(nullable = true)

    override fun convert(value: Double?) = value

    override fun reverse(value: Double?) = value
}
