package org.mybop.influxbd.resultmapper.mapping

import org.influxdb.dto.QueryResult
import org.mybop.influxbd.resultmapper.Key
import org.mybop.influxbd.resultmapper.MappingException
import kotlin.reflect.KClass

internal class ClassReader<K : Any> internal constructor(
        private val clazz: KClass<K>,
        private val timeMapping: TimeMapping<K, *>,
        private val fieldMappings: Set<FieldMapping<K, *, *, *>>,
        private val tagMappings: Set<TagMapping<K, *>>,
        private val otherFieldsMapping: FieldsMapping<K>?
) {

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
                .associate {
                    val key = parseKey(it)
                    Pair(key, parseSeries(key, it))
                }
    }

    private fun parseKey(series: QueryResult.Series): Key {
        if (series.tags?.isEmpty() != false) {
            return Key.EMPTY
        }

        return Key(
                series.tags.entries
                        .filter { it.value != null }
                        .filter { it.value.isNotEmpty() }
                        .associate { Pair(it.key, findTagMapper(it.key).parseResult(it.value)) }
        )
    }

    private fun findTagMapper(name: String): TagMapping<K, *> {
        return tagMappings.find {
            it.mappedName == name
        } ?: throw MappingException("No tag mapping for key $name")
    }

    private fun parseSeries(key: Key, series: QueryResult.Series): List<K> {
        if (series.values?.isEmpty() != false) {
            return emptyList()
        }

        return series.values.map { values ->
            parseModel(values[0] as String, key, series.columns.drop(1).zip(values.drop(1)).associate { it })
        }
    }

    private fun parseModel(time: String, key: Key, columns: Map<String, Any>): K {
        val properties = listOf<Pair<String, Any?>>(readTime(time))
                .plus(columns.filter { isField(it.key) }
                        .map { readField<Any?, Any?>(it.key, it.value) })
                .plus(columns.filter { isTag(it.key) }
                        .map { readTag<Any?>(it.key, it.value as String?) })
                .associate { it }
                .plus(key.value)
                .plus(
                        otherFieldsMapping?.let {
                            mapOf(Pair(it.propertyName, columns
                                    .filter { !isField(it.key) && !isTag(it.key) })
                            )
                        } ?: emptyMap()
                )

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
            .find { it.parameters.all { attributes.contains(it.name) || it.type.isMarkedNullable } }
            ?: throw MappingException("No constructor available for class ${clazz.qualifiedName}")

    private fun <R : Any?> readTime(value: String): Pair<String, R> {
        @Suppress("UNCHECKED_CAST")
        return Pair(timeMapping.propertyName, (timeMapping as TimeMapping<K, R>).parseResult(value))
    }

    private fun isField(name: String) = fieldMappings.find { it.mappedName == name } != null

    private fun <R : Any?, S : Any?> readField(name: String, value: R): Pair<String, S> {
        @Suppress("UNCHECKED_CAST")
        val mapping = fieldMappings.first { it.mappedName == name } as FieldMapping<K, S, *, R>

        return Pair(mapping.propertyName, mapping.parseResult(value))
    }

    private fun isTag(name: String) = tagMappings.find { it.mappedName == name } != null

    private fun <R : Any?> readTag(name: String, value: String?): Pair<String, R> {
        @Suppress("UNCHECKED_CAST")
        val mapping = tagMappings.first { it.mappedName == name } as TagMapping<K, R>

        return Pair(mapping.propertyName, mapping.parseResult(value))
    }

    private fun <R : Any?> writeProperty(propertyName: String, obj: K, value: R) {
        @Suppress("UNCHECKED_CAST")
        val mapping = fieldMappings.plus(tagMappings).plus(timeMapping)
                .find { it.propertyName == propertyName } as? PropertyMapping<K, R, *, *, *>
                ?: throw MappingException("Mapping not found for property $propertyName")

        mapping.writeField(obj, value)
    }
}
