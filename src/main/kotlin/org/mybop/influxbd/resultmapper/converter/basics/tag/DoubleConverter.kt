package org.mybop.influxbd.resultmapper.converter.basics.tag

import org.mybop.influxbd.resultmapper.converter.TagConverter
import kotlin.reflect.KType
import kotlin.reflect.full.createType

internal class DoubleConverter : TagConverter<Double?> {

    override fun supportedType() = Double::class.createType(nullable = true)

    override fun convert(key: Double?) = key?.toString()

    override fun reverse(key: String?, type: KType) = key?.toDouble()
}
