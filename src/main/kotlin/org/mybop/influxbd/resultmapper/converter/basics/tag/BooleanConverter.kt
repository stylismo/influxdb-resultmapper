package org.mybop.influxbd.resultmapper.converter.basics.tag

import org.mybop.influxbd.resultmapper.converter.TagConverter
import kotlin.reflect.full.createType

class BooleanConverter : TagConverter<Boolean> {

    override fun supportedType() = Boolean::class.createType()

    override fun convert(key: Boolean) = key.toString()

    override fun reverse(key: String) = key.toBoolean()
}
