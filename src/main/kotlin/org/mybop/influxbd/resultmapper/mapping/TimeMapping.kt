package org.mybop.influxbd.resultmapper.mapping

import org.mybop.influxbd.resultmapper.ConverterRegistry
import org.mybop.influxbd.resultmapper.MappingException
import org.mybop.influxbd.resultmapper.Time
import org.mybop.influxbd.resultmapper.converter.TimeConverter
import java.beans.PropertyDescriptor
import java.lang.reflect.Method
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation

internal class TimeMapping<K : Any, T : Any?> constructor(
        private val property: KProperty1<K, T>,
        propertyDescriptor: PropertyDescriptor,
        registry: ConverterRegistry
) : PropertyMapping<K, T, Long, Long, String> {

    private val annotation: Time = property.findAnnotation()!!

    override val mappedName = annotation.name

    override val propertyName: String
        get() = property.name

    private val getter: Method = propertyDescriptor.readMethod
            ?: throw MappingException("Getter not found for property $propertyName")

    private val setter: Method? = propertyDescriptor.writeMethod

    private val converter: TimeConverter<T> =
            if (annotation.converter == TimeConverter::
                    class) {
                registry.findTimeConverterFor(property.returnType)
            } else {
                registry.findTimeConverter(annotation.converter)
            }

    @Suppress("UNCHECKED_CAST")
    override fun extractField(value: K) = converter.convert(getter.invoke(value) as T)

    override fun parseResult(res: String) = converter.reverse(res)

    fun precision() = converter.precision()

    override fun writeField(obj: K, value: T) {
        if (setter == null) {
            throw MappingException("Setter not found for property $propertyName")
        }

        setter.invoke(obj, value)
    }
}
