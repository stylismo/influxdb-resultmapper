package org.mybop.influxbd.resultmapper.converter.basics.tag

import org.mybop.influxbd.resultmapper.converter.TagConverter
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
import kotlin.reflect.jvm.jvmErasure

class EnumConverter : TagConverter<Enum<*>?> {

    override fun supportedType() = Enum::class.createType(arguments = listOf(KTypeProjection.STAR), nullable = true)

    override fun convert(key: Enum<*>?) = key?.name

    @Suppress("UNCHECKED_CAST")
    override fun reverse(key: String?, type: KType): Enum<*>? {
        return key?.let { name -> (type.jvmErasure.java as Class<Enum<*>>).enumConstants.find { it.name == name } }
    }
}
