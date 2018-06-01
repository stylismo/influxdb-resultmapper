package org.mybop.influxbd.resultmapper.converter.basics.field

import org.mybop.influxbd.resultmapper.converter.StringFieldConverter
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
import kotlin.reflect.jvm.jvmErasure

internal class EnumConverter : StringFieldConverter<Enum<*>?> {

    override fun supportedType() = Enum::class.createType(arguments = listOf(KTypeProjection.STAR), nullable = true)

    override fun convert(value: Enum<*>?) = value?.name

    @Suppress("UNCHECKED_CAST")
    override fun reverse(value: String?, type: KType): Enum<*>? {
        return value?.let { name -> (type.jvmErasure.java as Class<Enum<*>>).enumConstants.find { it.name == name } }
    }
}
