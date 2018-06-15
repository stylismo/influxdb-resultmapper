package org.mybop.influxbd.resultmapper.converter.basics.field

import org.mybop.influxbd.resultmapper.converter.IntegerFieldConverter
import kotlin.reflect.KType
import kotlin.reflect.full.createType

internal class LongConverter : IntegerFieldConverter<Long?> {

    override fun supportedType() = Long::class.createType(nullable = true)

    override fun convert(value: Long?) = value

    override fun reverse(value: Double?, type: KType) = value?.toLong()
}
