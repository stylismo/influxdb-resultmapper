package org.mybop.influxbd.resultmapper.mapping

import org.mybop.influxbd.resultmapper.MappingException
import java.beans.PropertyDescriptor
import java.lang.reflect.Method
import kotlin.reflect.KProperty1

internal class FieldsMapping<K : Any> internal constructor(
        private val property: KProperty1<K, Map<String, Any>>,
        propertyDescriptor: PropertyDescriptor
) {

    val propertyName: String
        get() = property.name

    private val getter: Method = propertyDescriptor.readMethod
            ?: throw MappingException("Getter not found for property $propertyName")

    @Suppress("UNCHECKED_CAST")
    fun extractValues(value: K) = getter.invoke(value) as Map<String, Any>
}
