package org.mybop.influxbd.resultmapper.converter.basics.tag

import org.mybop.influxbd.resultmapper.converter.TagConverter
import kotlin.reflect.KType
import kotlin.reflect.full.createType

class LongConverter : TagConverter<Long?> {

    override fun supportedType() = Long::class.createType(nullable = true)

    override fun convert(key: Long?) = key?.toString()

    override fun reverse(key: String?, type: KType) = key?.toLong()
}
