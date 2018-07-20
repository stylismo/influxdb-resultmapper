package org.mybop.influxbd.resultmapper.mapping

import org.influxdb.InfluxDB
import org.influxdb.dto.BatchPoints
import org.influxdb.dto.Point
import org.mybop.influxbd.resultmapper.MappingException
import org.mybop.influxbd.resultmapper.Measurement
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

internal class ClassWriter<K : Any> internal constructor(
        private val clazz: KClass<K>,
        private val timeMapping: TimeMapping<K, *>,
        private val fieldMappings: Set<FieldMapping<K, *, *, *>>,
        private val tagMappings: Set<TagMapping<K, *>>,
        private val otherFieldsMapping: FieldsMapping<K>?
) {
    private val measurement: Measurement = clazz.findAnnotation()
            ?: throw MappingException("Class ${clazz.qualifiedName} must be annotated with @Measurement")

    val measurementName: String
        get() =
            if (measurement.name.isBlank()) {
                clazz.simpleName ?: throw MappingException("No class name found for $clazz.")
            } else {
                measurement.name
            }

    val retentionPolicy: String?
        get() =
            if (measurement.retentionPolicy.isBlank()) {
                null
            } else {
                measurement.retentionPolicy
            }

    val database: String?
        get() =
            if (measurement.database.isBlank()) {
                null
            } else {
                measurement.database
            }

    fun toPoint(value: K): Point = Point.measurement(measurementName)
            .time(timeMapping.extractField(value), timeMapping.precision())
            .fields(fieldMappings.associate { Pair(it.mappedName, it.extractField(value)) }
                    .plus(otherFieldsMapping?.extractValues(value) ?: emptyMap())
                    .filter { it.value != null })
            .tag(tagMappings.associate { Pair(it.mappedName, it.extractField(value)) }.filter { it.value != null })
            .build()

    fun toBatchPoints(elements: Collection<K>, consistencyLevel: InfluxDB.ConsistencyLevel): BatchPoints {
        val builder = BatchPoints.database(database)
                .retentionPolicy(retentionPolicy)
                .consistency(consistencyLevel)

        elements.map(this::toPoint)
                .forEach { builder.point(it) }

        return builder.build()
    }
}
