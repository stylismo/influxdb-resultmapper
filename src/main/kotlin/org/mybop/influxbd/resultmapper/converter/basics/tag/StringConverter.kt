package org.mybop.influxbd.resultmapper.converter.basics.tag

import org.mybop.influxbd.resultmapper.converter.TagConverter
import kotlin.reflect.KType
import kotlin.reflect.full.createType

class StringConverter : TagConverter<String?> {

    override fun supportedType() = String::class.createType(nullable = true)

    override fun convert(key: String?) = key

    override fun reverse(key: String?, type: KType) = key
}
