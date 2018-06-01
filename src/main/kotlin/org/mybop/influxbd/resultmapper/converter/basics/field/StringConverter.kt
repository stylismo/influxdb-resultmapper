package org.mybop.influxbd.resultmapper.converter.basics.field

import org.mybop.influxbd.resultmapper.converter.StringFieldConverter
import kotlin.reflect.KType
import kotlin.reflect.full.createType

internal class StringConverter : StringFieldConverter<String?> {

    override fun supportedType() = String::class.createType(nullable = true)

    override fun convert(value: String?) = value

    override fun reverse(value: String?, type: KType) = value
}
