package org.mybop.influxbd.resultmapper.mapping

import org.mybop.influxbd.resultmapper.ConverterRegistry
import org.mybop.influxbd.resultmapper.Field
import org.mybop.influxbd.resultmapper.MappingException
import org.mybop.influxbd.resultmapper.Tag
import org.mybop.influxbd.resultmapper.Time
import java.beans.Introspector
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

class ClassMapping<K : Any>(
        val reader: ClassReader<K>,
        val writer: ClassWriter<K>
) {
    companion object {
        @JvmStatic
        fun <K : Any> read(clazz: Class<K>, registry: ConverterRegistry) = read(clazz.kotlin, registry)

        @JvmStatic
        fun <K : Any> read(clazz: KClass<K>, registry: ConverterRegistry): ClassMapping<K> {

            val beanInfo = Introspector.getBeanInfo(clazz.java)

            val timeMapping = clazz.memberProperties
                    .map {
                        Pair(it, it.findAnnotation<Time>())
                    }
                    .firstOrNull { (_, time) ->
                        time != null
                    }
                    ?.let { (property, time) ->
                        TimeMapping(property, beanInfo.propertyDescriptors.find { it.name == property.name }!!, registry)
                    }
                    ?: throw MappingException("No @Time property found in $clazz")

            val fieldMappings =
                    clazz.memberProperties
                            .map {
                                Pair(it, it.findAnnotation<Field>())
                            }
                            .filter { (_, field) -> field != null }
                            .map { (property, field) ->
                                FieldMapping<K, Any?, Any?>(property, beanInfo.propertyDescriptors.find { it.name == property.name }!!, registry)
                            }
                            .toSet()

            val tagMappings =
                    clazz.memberProperties
                            .map {
                                Pair(it, it.findAnnotation<Tag>())
                            }
                            .filter { (_, tag) -> tag != null }
                            .map { (property, tag) ->
                                TagMapping(
                                        property,
                                        beanInfo.propertyDescriptors.find { it.name == property.name }
                                                ?: throw MappingException("Unable to find property description of field `${property.name}` for type `${clazz.qualifiedName}`"),
                                        registry
                                )
                            }
                            .toSet()

            return ClassMapping(
                    ClassReader(clazz, timeMapping, fieldMappings, tagMappings),
                    ClassWriter(
                            clazz,
                            timeMapping,
                            fieldMappings,
                            tagMappings
                    )
            )
        }
    }
}
