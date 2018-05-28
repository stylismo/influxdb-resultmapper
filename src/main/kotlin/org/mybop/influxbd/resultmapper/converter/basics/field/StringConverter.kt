package org.mybop.influxbd.resultmapper.converter.basics.field

import org.mybop.influxbd.resultmapper.converter.FieldConverter
import kotlin.reflect.full.createType

class StringConverter : FieldConverter<String, String> {

    override fun supportedType() = String::class.createType()

    override fun convert(value: String) = value

    override fun reverse(value: String) = value
}
