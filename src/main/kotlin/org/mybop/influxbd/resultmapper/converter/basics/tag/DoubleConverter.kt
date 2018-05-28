package org.mybop.influxbd.resultmapper.converter.basics.tag

import org.mybop.influxbd.resultmapper.converter.TagConverter
import kotlin.reflect.full.createType

class DoubleConverter : TagConverter<Double> {

    override fun supportedType() = Double::class.createType()

    override fun convert(key: Double) = key.toString()

    override fun reverse(key: String) = key.toDouble()
}
