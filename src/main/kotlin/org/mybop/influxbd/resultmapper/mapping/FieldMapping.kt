package org.mybop.influxbd.resultmapper.mapping

import org.mybop.influxbd.resultmapper.ConverterRegistry
import org.mybop.influxbd.resultmapper.Field
import org.mybop.influxbd.resultmapper.MappingException
import org.mybop.influxbd.resultmapper.converter.FieldConverter
import java.beans.PropertyDescriptor
import java.lang.reflect.Method
import kotlin.reflect.KProperty1

internal class FieldMapping<K : Any, T : Any?, R : Any?> private constructor(
        override val mappedName: String,
        val property: KProperty1<K, T>,
        val getter: Method,
        val setter: Method?,
        val fieldConverter: FieldConverter<T, R>
) : PropertyMapping<K, T, R, R> {

    constructor(
            property: KProperty1<K, T>,
            propertyDescriptor: PropertyDescriptor,
            field: Field,
            registry: ConverterRegistry
    ) : this(
            mappedName =
            if (field.name.isBlank()) {
                property.name
            } else {
                field.name
            },
            property = property,
            getter = propertyDescriptor.readMethod,
            setter = propertyDescriptor.writeMethod,
            fieldConverter =
            if (field.converter == FieldConverter::class) {
                registry.findFieldConverterFor(property.returnType)
            } else {
                registry.findFieldConverter(field.converter)
            }
    )

    override val propertyName: String
        get() = property.name

    @Suppress("UNCHECKED_CAST")
    override fun extractField(value: K) = getter.invoke(value)?.let { fieldConverter.convert(it as T) }

    override fun parseResult(res: R) = fieldConverter.reverse(res)

    override fun writeField(obj: K, value: T) {
        if (setter == null) {
            throw MappingException("Setter not found for property $propertyName")
        }

        setter.invoke(obj, value)
    }
}
