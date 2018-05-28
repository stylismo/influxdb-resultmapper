package org.mybop.influxbd.resultmapper.converter.basics.tag

import org.mybop.influxbd.resultmapper.converter.TagConverter
import kotlin.reflect.full.createType

class IntegerConverter : TagConverter<Int?> {

    override fun supportedType() = Int::class.createType(nullable = true)

    override fun convert(key: Int?) = key?.toString()

    override fun reverse(key: String?) = key?.toInt()
}
