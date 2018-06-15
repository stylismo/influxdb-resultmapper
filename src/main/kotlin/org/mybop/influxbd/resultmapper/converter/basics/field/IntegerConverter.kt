package org.mybop.influxbd.resultmapper.converter.basics.field

import org.mybop.influxbd.resultmapper.converter.IntegerFieldConverter
import kotlin.reflect.KType
import kotlin.reflect.full.createType

internal class IntegerConverter : IntegerFieldConverter<Int?> {

    override fun supportedType() = Int::class.createType(nullable = true)

    override fun convert(value: Int?) = value?.toLong()

    override fun reverse(value: Double?, type: KType) = value?.toInt()
}
