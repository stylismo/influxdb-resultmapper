package org.mybop.influxbd.resultmapper.converter.basics.tag

import org.mybop.influxbd.resultmapper.converter.TagConverter
import kotlin.reflect.KType
import kotlin.reflect.full.createType

class FloatConverter : TagConverter<Float?> {

    override fun supportedType() = Float::class.createType(nullable = true)

    override fun convert(key: Float?) = key?.toString()

    override fun reverse(key: String?, type: KType) = key?.toFloat()
}
