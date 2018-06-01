package org.mybop.influxbd.resultmapper

import org.mybop.influxbd.resultmapper.mapping.ClassReader
import org.mybop.influxbd.resultmapper.mapping.ClassWriter
import org.mybop.influxbd.resultmapper.mapping.FieldMapping
import org.mybop.influxbd.resultmapper.mapping.TagMapping
import org.mybop.influxbd.resultmapper.mapping.TimeMapping
import java.beans.BeanInfo
import java.beans.Introspector
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

class ClassMappingIntrospector(
        private val registry: ConverterRegistry
) {

    fun <K : Any> mapper(clazz: Class<K>) = mapper(clazz.kotlin)

    fun <K : Any> mapper(clazz: KClass<K>): Pair<ClassReader<K>, ClassWriter<K>> {

        val beanInfo = Introspector.getBeanInfo(clazz.java)

        val timeMapping = readTimeMapping(clazz, beanInfo)

        val fieldMapping = readFieldMapping(clazz, beanInfo)

        val tagMapping = readTapMapping(clazz, beanInfo)

        return Pair(
                ClassReader(
                        clazz,
                        timeMapping,
                        fieldMapping,
                        tagMapping
                ),
                ClassWriter(
                        clazz,
                        timeMapping,
                        fieldMapping,
                        tagMapping
                ))
    }

    fun <K : Any> reader(clazz: Class<K>) = reader(clazz.kotlin)

    fun <K : Any> reader(clazz: KClass<K>): ClassReader<K> {

        val beanInfo = Introspector.getBeanInfo(clazz.java)

        return ClassReader(
                clazz,
                readTimeMapping(clazz, beanInfo),
                readFieldMapping(clazz, beanInfo),
                readTapMapping(clazz, beanInfo)
        )
    }

    fun <K : Any> writer(clazz: Class<K>) = writer(clazz.kotlin)

    fun <K : Any> writer(clazz: KClass<K>): ClassWriter<K> {

        val beanInfo = Introspector.getBeanInfo(clazz.java)

        return ClassWriter(
                clazz,
                readTimeMapping(clazz, beanInfo),
                readFieldMapping(clazz, beanInfo),
                readTapMapping(clazz, beanInfo)
        )
    }

    internal fun <K : Any> readTimeMapping(clazz: KClass<K>, beanInfo: BeanInfo) =
            clazz.memberProperties
                    .map {
                        Pair(it, it.findAnnotation<Time>())
                    }
                    .firstOrNull { (_, time) ->
                        time != null
                    }
                    ?.let { (property, _) ->
                        TimeMapping(property, beanInfo.propertyDescriptors.find { it.name == property.name }!!, registry)
                    }
                    ?: throw MappingException("No @Time property found in $clazz")

    internal fun <K : Any> readFieldMapping(clazz: KClass<K>, beanInfo: BeanInfo) = clazz.memberProperties
            .map {
                kotlin.Pair(it, it.findAnnotation<Field>())
            }
            .filter { (_, field) -> field != null }
            .map { (property, _) ->
                FieldMapping<K, Any?, Any?>(property, beanInfo.propertyDescriptors.find { it.name == property.name }!!, registry)
            }
            .toSet()

    internal fun <K : Any> readTapMapping(clazz: KClass<K>, beanInfo: BeanInfo) =
            clazz.memberProperties
                    .map {
                        Pair(it, it.findAnnotation<Tag>())
                    }
                    .filter { (_, tag) -> tag != null }
                    .map { (property, _) ->
                        TagMapping(
                                property,
                                beanInfo.propertyDescriptors.find { it.name == property.name }
                                        ?: throw MappingException("Unable to find property description of field `${property.name}` for type `${clazz.qualifiedName}`"),
                                registry
                        )
                    }
                    .toSet()
}
