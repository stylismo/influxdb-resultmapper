package org.mybop.influxbd.resultmapper.mapping

import org.influxdb.dto.Point
import org.influxdb.dto.QueryResult
import org.mybop.influxbd.resultmapper.ConverterRegistry
import org.mybop.influxbd.resultmapper.Field
import org.mybop.influxbd.resultmapper.Key
import org.mybop.influxbd.resultmapper.MappingException
import org.mybop.influxbd.resultmapper.Measurement
import org.mybop.influxbd.resultmapper.Tag
import org.mybop.influxbd.resultmapper.Time
import org.mybop.influxbd.resultmapper.converter.TimeConverter
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

internal class ClassMapping<T : Any> private constructor(
        private val clazz: KClass<T>,
        private val measurementName: String,
        private val timeMapping: TimeMapping<T, *>,
        private val fieldMappings: Set<FieldMapping<T, *, *>>,
        private val tagMappings: Set<TagMapping<T, *>>
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

    fun toPoint(value: T): Point = Point.measurement(measurementName)
            .time(timeMapping.extractTime(value), timeMapping.precision())
            .fields(fieldMappings.associate { Pair(it.name, it.extractField(value)) })
            .tag(tagMappings.associate { Pair(it.name, it.extractTag(value)) })
            .build()

    fun parseQueryResult(queryResult: QueryResult): List<Map<Key, List<T>>> {
        if (queryResult.hasError()) {
            throw MappingException(queryResult.error)
        }

        if (queryResult.results?.isEmpty() != false) {
            return emptyList()
        }

        return queryResult.results
                .map { parseResult(it) }
    }

    private fun parseResult(result: QueryResult.Result): Map<Key, List<T>> {
        if (result.hasError()) {
            throw MappingException(result.error)
        }

        if (result.series?.isEmpty() != false) {
            return emptyMap()
        }

        return result.series
                .associate { Pair(parseKey(it), parseSeries(it)) }
    }

    private fun parseKey(series: QueryResult.Series): Key {
        if (series.tags?.isEmpty() != false) {
            return Key.EMPTY
        }

        return Key(
                series.tags.entries
                        .associate { Pair(it.key, findTagMapper(it.key).tagConverter.reverse(it.value)) }
        )
    }

    private fun findTagMapper(name: String): TagMapping<T, *> {
        return tagMappings.find {
            it.name == name
        } ?: throw MappingException("No tag mapping for key $name")
    }

    private fun parseSeries(series: QueryResult.Series): List<T> {
        if (series.values?.isEmpty() != false) {
            return emptyList()
        }

        return series.values.map { values ->
            parseModel(values[0] as String, series.columns.drop(1).zip(values.drop(1)).associate { it }
                    .plus(series.tags ?: emptyMap()))
        }
    }

    private fun parseModel(time: String, columns: Map<String, Any>): T {
        val properties = listOf<Pair<String, Any?>>(readTime(time))
                .plus(columns.map {
                    when {
                        isField(it.key) -> readField<Any?, Any?>(it.key, it.value)
                        isTag(it.key) -> readTag(it.key, it.value as String)
                        else -> throw MappingException("Unknown column ${it.key}")
                    }
                })
                .associate { it }

        val constructor = findConstructor(properties.keys)

        val result = constructor.callBy(
                constructor.parameters.associate {
                    Pair(it, properties[it.name])
                }
        )

        properties
                .filterKeys { property ->
                    constructor.parameters.none { it.name == property }
                }
                .forEach { entry ->
                    val property = clazz.memberProperties.find { it.name == entry.key }
                            as KMutableProperty1<T, Any?>
                    property.set(result, entry.value)
                }

        return result
    }

    private fun findConstructor(attributes: Collection<String>) = clazz.constructors
            .sortedByDescending { it.parameters.size }
            .first { it.parameters.map { it.name }.containsAll(attributes) }

    private fun <R : Any?> readTime(value: String): Pair<String, R> {
        @Suppress("UNCHECKED_CAST")
        return Pair(timeMapping.property.name, (timeMapping.timeConverter as TimeConverter<R>).reverse(value))
    }

    private fun isField(name: String) = fieldMappings.find { it.name == name } != null

    private fun <R : Any?, S : Any?> readField(name: String, value: R): Pair<String, S> {
        @Suppress("UNCHECKED_CAST")
        val mapping = fieldMappings.first { it.name == name } as FieldMapping<T, S, R>

        return Pair(mapping.property.name, mapping.fieldConverter.reverse(value))
    }

    private fun isTag(name: String) = tagMappings.find { it.name == name } != null

    private fun <R : Any?> readTag(name: String, value: String?): Pair<String, R> {
        @Suppress("UNCHECKED_CAST")
        val mapping = tagMappings.first { it.name == name } as TagMapping<T, R>

        return Pair(mapping.property.name, mapping.tagConverter.reverse(value))
    }

    companion object {
        fun <T : Any> read(clazz: KClass<T>, registry: ConverterRegistry): ClassMapping<T> =
                ClassMapping(clazz, clazz.findAnnotation()
                        ?: throw MappingException("Class $clazz not annotated with @Measurement."),
                        registry
                )
    }
}
