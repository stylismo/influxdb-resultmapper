package org.mybop.influxbd.resultmapper.mapping

import org.mybop.influxbd.resultmapper.ConverterRegistry
import org.mybop.influxbd.resultmapper.MappingException
import org.mybop.influxbd.resultmapper.Time
import org.mybop.influxbd.resultmapper.converter.TimeConverter
import java.beans.PropertyDescriptor
import java.lang.reflect.Method
import kotlin.reflect.KProperty1

internal class TimeMapping<K : Any, T : Any?> private constructor(
        val property: KProperty1<K, T>,
        val getter: Method,
        val setter: Method?,
        val timeConverter: TimeConverter<T>
) : PropertyMapping<K, T, Long, String> {

    override val mappedName = "time"

    override val propertyName: String
        get() = property.name

    constructor(
            property: KProperty1<K, T>,
            propertyDescriptor: PropertyDescriptor,
            time: Time,
            registry: ConverterRegistry
    ) : this(
            property = property,
            getter = propertyDescriptor.readMethod,
            setter = propertyDescriptor.writeMethod,
            timeConverter = if (time.converter == TimeConverter::
                    class) {
                registry.findTimeConverterFor(property.returnType)
            } else {
                registry.findTimeConverter(time.converter)
            }
    )

    @Suppress("UNCHECKED_CAST")
    override fun extractField(value: K) = timeConverter.convert(getter.invoke(value) as T)

    override fun parseResult(res: String) = timeConverter.reverse(res)

    fun precision() = timeConverter.precision()

    override fun writeField(obj: K, value: T) {
        if (setter == null) {
            throw MappingException("Setter not found for property $propertyName")
        }

        setter.invoke(obj, value)
    }
}
