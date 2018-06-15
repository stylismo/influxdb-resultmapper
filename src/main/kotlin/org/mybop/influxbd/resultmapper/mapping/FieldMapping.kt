package org.mybop.influxbd.resultmapper.mapping

import org.mybop.influxbd.resultmapper.ConverterRegistry
import org.mybop.influxbd.resultmapper.Field
import org.mybop.influxbd.resultmapper.MappingException
import org.mybop.influxbd.resultmapper.converter.FieldConverter
import java.beans.PropertyDescriptor
import java.lang.reflect.Method
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation

internal class FieldMapping<K : Any, T : Any?, D : Any?, R : Any?> constructor(
        private val property: KProperty1<K, T>,
        propertyDescriptor: PropertyDescriptor,
        registry: ConverterRegistry
) : PropertyMapping<K, T, D, R, R> {

    private val annotation: Field = property.findAnnotation()!!

    override val mappedName: String
        get() =
            if (annotation.name.isBlank()) {
                property.name
            } else {
                annotation.name
            }

    override val propertyName: String
        get() = property.name

    private val getter: Method = propertyDescriptor.readMethod
            ?: throw MappingException("Getter not found for property $propertyName")

    private val setter: Method? = propertyDescriptor.writeMethod

    private val converter: FieldConverter<T, D, R> =
            if (annotation.converter == FieldConverter::class) {
                registry.findFieldConverterFor(property.returnType)
            } else {
                registry.findFieldConverter(annotation.converter)
            }

    @Suppress("UNCHECKED_CAST")
    override fun extractField(value: K): D? = getter.invoke(value)?.let { converter.convert(it as T) }

    override fun parseResult(res: R) = converter.reverse(res, property.returnType)

    override fun writeField(obj: K, value: T) {
        if (setter == null) {
            throw MappingException("Setter not found for property $propertyName")
        }

        setter.invoke(obj, value)
    }
}
