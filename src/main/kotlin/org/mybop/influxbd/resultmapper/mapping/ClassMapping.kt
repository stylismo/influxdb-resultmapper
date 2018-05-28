package org.mybop.influxbd.resultmapper.mapping

import org.influxdb.dto.Point
import org.mybop.influxbd.resultmapper.ConverterRegistry
import org.mybop.influxbd.resultmapper.Field
import org.mybop.influxbd.resultmapper.MappingException
import org.mybop.influxbd.resultmapper.Measurement
import org.mybop.influxbd.resultmapper.Tag
import org.mybop.influxbd.resultmapper.Time
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

internal data class ClassMapping<T : Any> private constructor(
        val clazz: KClass<T>,
        val measurementName: String,
        val timeMapping: TimeMapping<T, *>,
        val fieldMappings: Set<FieldMapping<T, *, *>>,
        val tagMappings: Set<TagMapping<T, *>>
) {
    constructor(
            clazz: KClass<T>,
            measurement: Measurement,
            registry: ConverterRegistry
    ) : this(
            clazz = clazz,
            measurementName =
            if (measurement.name.isBlank()) {
                clazz.simpleName ?: throw MappingException("No class name found for $clazz.")
            } else {
                measurement.name
            },
            timeMapping =
            clazz.memberProperties
                    .map {
                        Pair(it, it.findAnnotation<Time>())
                    }
                    .firstOrNull { (_, time) ->
                        time != null
                    }
                    ?.let { (property, time) ->
                        TimeMapping(property, time!!, registry)
                    }
                    ?: throw MappingException("No @Time property found in $clazz"),
            fieldMappings =
            clazz.memberProperties
                    .map {
                        Pair(it, it.findAnnotation<Field>())
                    }
                    .filter { (_, field) -> field != null }
                    .map { (property, field) ->
                        FieldMapping<T, Any?, Any?>(property, field!!, registry)
                    }
                    .toSet(),
            tagMappings =
            clazz.memberProperties
                    .map {
                        Pair(it, it.findAnnotation<Tag>())
                    }
                    .filter { (_, tag) -> tag != null }
                    .map { (property, tag) ->
                        TagMapping<T, Any?>(property, tag!!, registry)
                    }
                    .toSet()
    )

    fun toPoint(value: T) = Point.measurement(measurementName)
            .time(timeMapping.extractTime(value), timeMapping.precision())
            .fields(fieldMappings.associate { Pair(it.name, it.extractField(value)) })
            .tag(tagMappings.associate { Pair(it.name, it.extractTag(value)) })
            .build()

    companion object {
        fun <T : Any> read(clazz: KClass<T>, registry: ConverterRegistry): ClassMapping<T> =
                ClassMapping(clazz, clazz.findAnnotation()
                        ?: throw MappingException("Class $clazz not annotated with @Measurement."),
                        registry
                )
    }
}
