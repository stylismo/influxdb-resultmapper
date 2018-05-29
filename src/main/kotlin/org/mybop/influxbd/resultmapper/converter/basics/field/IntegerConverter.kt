package org.mybop.influxbd.resultmapper.converter.basics.field

import org.mybop.influxbd.resultmapper.converter.NumberFieldConverter
import kotlin.reflect.full.createType

class IntegerConverter : NumberFieldConverter<Int?> {

    override fun supportedType() = Int::class.createType(nullable = true)

    override fun convert(value: Int?) = value?.toDouble()

    override fun reverse(value: Double?) = value?.toInt()
}
