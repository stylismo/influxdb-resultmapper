package org.mybop.influxbd.resultmapper.mapping

import org.mybop.influxbd.resultmapper.ConverterRegistry
import org.mybop.influxbd.resultmapper.Time
import org.mybop.influxbd.resultmapper.converter.TimeConverter
import kotlin.reflect.KProperty1

internal data class TimeMapping<K, T> private constructor(
        val property: KProperty1<K, T>,
        val timeConverter: TimeConverter<T>
) {
    constructor(
            property: KProperty1<K, T>,
            time: Time,
            registry: ConverterRegistry
    ) : this(
            property = property,
            timeConverter = if (time.converter == TimeConverter::class) {
                registry.findTimeConverterFor(property.returnType)
            } else {
                registry.findTimeConverter(time.converter)
            }
    )

    fun extractTime(value: K): Long = timeConverter.convert(property.get(value))

    fun precision() = timeConverter.precision()
}
