package org.mybop.influxbd.resultmapper.mapping

import org.mybop.influxbd.resultmapper.ConverterRegistry
import org.mybop.influxbd.resultmapper.MappingException
import org.mybop.influxbd.resultmapper.Tag
import org.mybop.influxbd.resultmapper.converter.TagConverter
import java.beans.PropertyDescriptor
import java.lang.reflect.Method
import kotlin.reflect.KProperty1

internal class TagMapping<K : Any, T : Any?> private constructor(
        override val mappedName: String,
        val property: KProperty1<K, T>,
        val getter: Method,
        val setter: Method?,
        val tagConverter: TagConverter<T>
) : PropertyMapping<K, T, String?, String?> {

    constructor(
            property: KProperty1<K, T>,
            propertyDescriptor: PropertyDescriptor,
            tag: Tag,
            registry: ConverterRegistry
    ) : this(
            mappedName =
            if (tag.name.isBlank()) {
                property.name
            } else {
                tag.name
            },
            getter = propertyDescriptor.readMethod,
            setter = propertyDescriptor.writeMethod,
            property = property,
            tagConverter =
            if (tag.converter == TagConverter::class) {
                registry.findTagConverterFor(property.returnType)
            } else {
                registry.findTagConverter(tag.converter)
            }
    )

    override val propertyName: String
        get() = property.name

    @Suppress("UNCHECKED_CAST")
    override fun extractField(value: K) = getter.invoke(value)?.let { tagConverter.convert(it as T) }

    override fun parseResult(res: String?) = tagConverter.reverse(res)

    override fun writeField(obj: K, value: T) {
        if (setter == null) {
            throw MappingException("Setter not found for property $propertyName")
        }

        setter.invoke(obj, value)
    }
}

