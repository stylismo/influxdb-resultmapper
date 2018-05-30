package org.mybop.influxbd.resultmapper.converter.basics.field

import org.mybop.influxbd.resultmapper.converter.BooleanFieldConverter
import kotlin.reflect.KType
import kotlin.reflect.full.createType

class BooleanConverter : BooleanFieldConverter<Boolean?> {

    override fun supportedType() = Boolean::class.createType(nullable = true)

    override fun convert(value: Boolean?) = value

    override fun reverse(value: Boolean?, type: KType) = value
}
