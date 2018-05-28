package org.mybop.influxbd.resultmapper.converter.basics.field

import org.mybop.influxbd.resultmapper.converter.StringFieldConverter
import kotlin.reflect.full.createType

class StringConverter : StringFieldConverter<String?> {

    override fun supportedType() = String::class.createType(nullable = true)

    override fun convert(value: String?) = value

    override fun reverse(value: String?) = value
}
