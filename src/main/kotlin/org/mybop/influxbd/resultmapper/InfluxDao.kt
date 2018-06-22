package org.mybop.influxbd.resultmapper

import org.influxdb.InfluxDB
import org.influxdb.dto.Query
import org.mybop.influxbd.resultmapper.mapping.ClassMappingIntrospector
import org.mybop.influxbd.resultmapper.mapping.ClassReader
import org.mybop.influxbd.resultmapper.mapping.ClassWriter
import kotlin.reflect.KClass

class InfluxDao<K : Any>(
        clazz: KClass<K>,
        private val registry: ConverterRegistry,
        private val client: InfluxDB
) {

    constructor(clazz: Class<K>, registry: ConverterRegistry, client: InfluxDB) : this(clazz.kotlin, registry, client)

    var consistencyLevel: InfluxDB.ConsistencyLevel = InfluxDB.ConsistencyLevel.ONE

    private val reader: ClassReader<K>

    private val writer: ClassWriter<K>

    val database
        get() = writer.database

    val retentionPolicy
        get() = writer.retentionPolicy

    val measurementName
        get() = writer.measurementName

    init {
        val (reader, writer) = ClassMappingIntrospector.mapper(clazz, registry)
        this.reader = reader
        this.writer = writer
    }

    fun save(element: K) = save(listOf(element))

    fun save(elements: Collection<K>) {
        client.write(writer.toBatchPoints(elements, consistencyLevel))
    }

    fun execute(query: String) {
        query(query)
    }

    fun queryOne(query: String) = queryList(query).firstOrNull()

    fun <T : Any> queryOne(query: String, clazz: KClass<T>) = queryList(query, clazz).firstOrNull()

    fun <T : Any> queryOne(query: String, clazz: Class<T>) = queryOne(query, clazz.kotlin)

    fun queryList(query: String) = query(query).first().flatMap { it.value }

    fun <T : Any> queryList(query: String, clazz: KClass<T>) = query(query, clazz).first().flatMap { it.value }

    fun <T : Any> queryList(query: String, clazz: Class<T>) = queryList(query, clazz.kotlin)

    fun queryGroup(query: String) = query(query).first()

    fun <T : Any> queryGroup(query: String, clazz: KClass<T>) = query(query, clazz).first()

    fun <T : Any> queryGroup(query: String, clazz: Class<T>) = queryGroup(query, clazz.kotlin)

    fun query(query: String): List<Map<Key, List<K>>> =
            reader.parseQueryResult(
                    client.query(Query(query, database))
            )

    fun <T : Any> query(query: String, clazz: KClass<T>): List<Map<Key, List<T>>> =
            ClassMappingIntrospector.reader(clazz, registry).parseQueryResult(
                    client.query(Query(query, database))
            )

    fun <T : Any> query(query: String, clazz: Class<T>) = query(query, clazz.kotlin)
}
