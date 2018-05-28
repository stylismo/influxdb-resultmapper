package org.mybop.influxbd.resultmapper.mapping

import org.mybop.influxbd.resultmapper.ConverterRegistry
import org.mybop.influxbd.resultmapper.Tag
import org.mybop.influxbd.resultmapper.converter.TagConverter
import kotlin.reflect.KProperty1

internal data class TagMapping<K, T> private constructor(
        val name: String,
        val property: KProperty1<K, T>,
        val tagConverter: TagConverter<T>
) {
    constructor(
            property: KProperty1<K, T>,
            tag: Tag,
            registry: ConverterRegistry
    ) : this(
            name =
            if (tag.name.isBlank()) {
                property.name
            } else {
                tag.name
            },
            property = property,
            tagConverter =
            if (tag.converter == TagConverter::class) {
                registry.findTagConverterFor(property.returnType)
            } else {
                registry.findTagConverter(tag.converter)
            }
    )

    fun extractTag(value: K) = tagConverter.convert(property.get(value))
}

