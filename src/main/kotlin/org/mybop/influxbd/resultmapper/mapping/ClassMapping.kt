package org.mybop.influxbd.resultmapper.mapping

import org.influxdb.dto.Point
import org.influxdb.dto.QueryResult
import org.mybop.influxbd.resultmapper.*
import java.beans.BeanInfo
import java.beans.Introspector
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

class ClassMapping<K : Any> private constructor(
        private val clazz: KClass<K>,
        private val measurementName: String,
        private val timeMapping: TimeMapping<K, *>,
        private val fieldMappings: Set<FieldMapping<K, *, *>>,
        private val tagMappings: Set<TagMapping<K, *>>
) {
    constructor(
            clazz: KClass<K>,
            beanInfo: BeanInfo,
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
                        TimeMapping(property, beanInfo.propertyDescriptors.find { it.name == property.name }!!, time!!, registry)
                    }
                    ?: throw MappingException("No @Time property found in $clazz"),
            fieldMappings =
            clazz.memberProperties
                    .map {
                        Pair(it, it.findAnnotation<Field>())
                    }
                    .filter { (_, field) -> field != null }
                    .map { (property, field) ->
                        FieldMapping<K, Any?, Any?>(property, beanInfo.propertyDescriptors.find { it.name == property.name }!!, field!!, registry)
                    }
                    .toSet(),
            tagMappings =
            clazz.memberProperties
                    .map {
                        Pair(it, it.findAnnotation<Tag>())
                    }
                    .filter { (_, tag) -> tag != null }
                    .map { (property, tag) ->
                        TagMapping<K, Any?>(property, beanInfo.propertyDescriptors.find { it.name == property.name }!!, tag!!, registry)
                    }
                    .toSet()
    )

    fun toPoint(value: K): Point = Point.measurement(measurementName)
            .time(timeMapping.extractField(value), timeMapping.precision())
            .fields(fieldMappings.associate { Pair(it.mappedName, it.extractField(value)) })
            .tag(tagMappings.associate { Pair(it.mappedName, it.extractField(value)) })
            .build()

    fun parseQueryResult(queryResult: QueryResult): List<Map<Key, List<K>>> {
        if (queryResult.hasError()) {
            throw MappingException(queryResult.error)
        }

        if (queryResult.results?.isEmpty() != false) {
            return emptyList()
        }

        return queryResult.results
                .map { parseResult(it) }
    }

    private fun parseResult(result: QueryResult.Result): Map<Key, List<K>> {
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
                        .associate { Pair(it.key, findTagMapper(it.key).parseResult(it.value)) }
        )
    }

    private fun findTagMapper(name: String): TagMapping<K, *> {
        return tagMappings.find {
            it.mappedName == name
        } ?: throw MappingException("No tag mapping for key $name")
    }

    private fun parseSeries(series: QueryResult.Series): List<K> {
        if (series.values?.isEmpty() != false) {
            return emptyList()
        }

        return series.values.map { values ->
            parseModel(values[0] as String, series.columns.drop(1).zip(values.drop(1)).associate { it }
                    .plus(series.tags ?: emptyMap()))
        }
    }

    private fun parseModel(time: String, columns: Map<String, Any>): K {
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
                .forEach { property ->
                    writeProperty(property.key, result, property.value)
                }

        return result
    }

    private fun findConstructor(attributes: Collection<String>) = clazz.constructors
            .sortedByDescending { it.parameters.size }
            .find { attributes.containsAll(it.parameters.map { it.name }) }
            ?: throw MappingException("No constructor available for class ${clazz.qualifiedName}")

    private fun <R : Any?> readTime(value: String): Pair<String, R> {
        @Suppress("UNCHECKED_CAST")
        return Pair(timeMapping.property.name, (timeMapping as TimeMapping<K, R>).parseResult(value))
    }

    private fun isField(name: String) = fieldMappings.find { it.mappedName == name } != null

    private fun <R : Any?, S : Any?> readField(name: String, value: R): Pair<String, S> {
        @Suppress("UNCHECKED_CAST")
        val mapping = fieldMappings.first { it.mappedName == name } as FieldMapping<K, S, R>

        return Pair(mapping.property.name, mapping.parseResult(value))
    }

    private fun isTag(name: String) = tagMappings.find { it.mappedName == name } != null

    private fun <R : Any?> readTag(name: String, value: String?): Pair<String, R> {
        @Suppress("UNCHECKED_CAST")
        val mapping = tagMappings.first { it.mappedName == name } as TagMapping<K, R>

        return Pair(mapping.property.name, mapping.parseResult(value))
    }

    private fun <R : Any?> writeProperty(propertyName: String, obj: K, value: R) {
        @Suppress("UNCHECKED_CAST")
        val mapping = fieldMappings.plus(tagMappings).plus(timeMapping)
                .find { it.propertyName == propertyName } as? PropertyMapping<K, R, *, *>
                ?: throw MappingException("Mapping not found for property $propertyName")

        mapping.writeField(obj, value)
    }

    companion object {
        fun <T : Any> read(clazz: Class<T>, registry: ConverterRegistry) = read(clazz.kotlin, registry)

        fun <T : Any> read(clazz: KClass<T>, registry: ConverterRegistry): ClassMapping<T> =
                ClassMapping(
                        clazz,
                        Introspector.getBeanInfo(clazz.java),
                        clazz.findAnnotation()
                                ?: throw MappingException("Class $clazz not annotated with @Measurement."),
                        registry
                )
    }
}
