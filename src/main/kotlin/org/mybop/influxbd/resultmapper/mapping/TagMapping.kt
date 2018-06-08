package org.mybop.influxbd.resultmapper.mapping

import org.mybop.influxbd.resultmapper.ConverterRegistry
import org.mybop.influxbd.resultmapper.MappingException
import org.mybop.influxbd.resultmapper.Tag
import org.mybop.influxbd.resultmapper.converter.TagConverter
import java.beans.PropertyDescriptor
import java.lang.reflect.Method
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation

internal class TagMapping<K : Any, T : Any?> constructor(
        private val property: KProperty1<K, T>,
        propertyDescriptor: PropertyDescriptor,
        registry: ConverterRegistry
) : PropertyMapping<K, T, String?, String?> {

    private val annotation: Tag = property.findAnnotation()!!

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

    private val converter: TagConverter<T> =
            if (annotation.converter == TagConverter::class) {
                registry.findTagConverterFor(property.returnType)
            } else {
                registry.findTagConverter(annotation.converter)
            }

    @Suppress("UNCHECKED_CAST")
    override fun extractField(value: K) = getter.invoke(value)?.let { converter.convert(it as T) }

    override fun parseResult(res: String?) =
            converter.reverse(
                    if (res?.isEmpty() != false) {
                        null
                    } else {
                        res
                    },
                    property.returnType
            )

    override fun writeField(obj: K, value: T) {
        if (setter == null) {
            throw MappingException("Setter not found for property $propertyName")
        }

        setter.invoke(obj, value)
    }
}

