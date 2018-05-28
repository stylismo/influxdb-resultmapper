package org.mybop.influxbd.resultmapper.converter.basics.tag

import org.mybop.influxbd.resultmapper.converter.TagConverter
import kotlin.reflect.full.createType

class StringConverter : TagConverter<String> {

    override fun supportedType() = String::class.createType()

    override fun convert(key: String) = key

    override fun reverse(key: String) = key
}
