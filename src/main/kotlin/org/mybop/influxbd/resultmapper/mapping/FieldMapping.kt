package org.mybop.influxbd.resultmapper.mapping

import org.mybop.influxbd.resultmapper.ConverterRegistry
import org.mybop.influxbd.resultmapper.Field
import org.mybop.influxbd.resultmapper.converter.FieldConverter
import kotlin.reflect.KProperty1

internal data class FieldMapping<K, T, R> private constructor(
        val name: String,
        val property: KProperty1<K, T>,
        val fieldConverter: FieldConverter<T, R>
) {
    constructor(
            property: KProperty1<K, T>,
            field: Field,
            registry: ConverterRegistry
    ) : this(
            name =
            if (field.name.isBlank()) {
                property.name
            } else {
                field.name
            },
            property = property,
            fieldConverter =
            if (field.converter == FieldConverter::class) {
                registry.findFieldConverterFor(property.returnType)
            } else {
                registry.findFieldConverter(field.converter)
            }
    )

    fun extractField(value: K) = fieldConverter.convert(property.get(value))
}
