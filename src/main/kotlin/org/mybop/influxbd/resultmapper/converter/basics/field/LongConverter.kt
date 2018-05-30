package org.mybop.influxbd.resultmapper.converter.basics.field

import org.mybop.influxbd.resultmapper.converter.NumberFieldConverter
import kotlin.reflect.KType
import kotlin.reflect.full.createType

class LongConverter : NumberFieldConverter<Long?> {

    override fun supportedType() = Long::class.createType(nullable = true)

    override fun convert(value: Long?) = value?.toDouble()

    override fun reverse(value: Double?, type: KType) = value?.toLong()
}
