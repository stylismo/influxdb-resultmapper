package org.mybop.influxbd.resultmapper.converter.basics.tag

import org.mybop.influxbd.resultmapper.converter.TagConverter
import kotlin.reflect.KType
import kotlin.reflect.full.createType

internal class BooleanConverter : TagConverter<Boolean?> {

    override fun supportedType() = Boolean::class.createType(nullable = true)

    override fun convert(key: Boolean?) = key?.toString()

    override fun reverse(key: String?, type: KType) = key?.toBoolean()
}
