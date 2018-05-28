package org.mybop.influxbd.resultmapper.converter.basics.tag

import org.mybop.influxbd.resultmapper.converter.TagConverter
import kotlin.reflect.full.createType

class LongConverter : TagConverter<Long> {

    override fun supportedType() = Long::class.createType()

    override fun convert(key: Long) = key.toString()

    override fun reverse(key: String) = key.toLong()
}
