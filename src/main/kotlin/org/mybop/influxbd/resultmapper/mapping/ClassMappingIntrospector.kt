package org.mybop.influxbd.resultmapper.mapping

import org.mybop.influxbd.resultmapper.ConverterRegistry
import org.mybop.influxbd.resultmapper.Field
import org.mybop.influxbd.resultmapper.MappingException
import org.mybop.influxbd.resultmapper.Tag
import org.mybop.influxbd.resultmapper.Time
import java.beans.BeanInfo
import java.beans.Introspector
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

internal object ClassMappingIntrospector {

    fun <K : Any> mapper(clazz: Class<K>, registry: ConverterRegistry) = mapper(clazz.kotlin, registry)

    fun <K : Any> mapper(clazz: KClass<K>, registry: ConverterRegistry): Pair<ClassReader<K>, ClassWriter<K>> {

        val beanInfo = Introspector.getBeanInfo(clazz.java)

        val timeMapping = readTimeMapping(clazz, beanInfo, registry)

        val fieldMapping = readFieldMapping(clazz, beanInfo, registry)

        val tagMapping = readTapMapping(clazz, beanInfo, registry)

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

    fun <K : Any> reader(clazz: Class<K>, registry: ConverterRegistry) = reader(clazz.kotlin, registry)

    fun <K : Any> reader(clazz: KClass<K>, registry: ConverterRegistry): ClassReader<K> {

        val beanInfo = Introspector.getBeanInfo(clazz.java)

        return ClassReader(
                clazz,
                readTimeMapping(clazz, beanInfo, registry),
                readFieldMapping(clazz, beanInfo, registry),
                readTapMapping(clazz, beanInfo, registry)
        )
    }

    fun <K : Any> writer(clazz: Class<K>, registry: ConverterRegistry) = writer(clazz.kotlin, registry)

    fun <K : Any> writer(clazz: KClass<K>, registry: ConverterRegistry): ClassWriter<K> {

        val beanInfo = Introspector.getBeanInfo(clazz.java)

        return ClassWriter(
                clazz,
                readTimeMapping(clazz, beanInfo, registry),
                readFieldMapping(clazz, beanInfo, registry),
                readTapMapping(clazz, beanInfo, registry)
        )
    }

    private fun <K : Any> readTimeMapping(clazz: KClass<K>, beanInfo: BeanInfo, registry: ConverterRegistry) =
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

    private fun <K : Any> readFieldMapping(clazz: KClass<K>, beanInfo: BeanInfo, registry: ConverterRegistry) = clazz.memberProperties
            .map {
                kotlin.Pair(it, it.findAnnotation<Field>())
            }
            .filter { (_, field) -> field != null }
            .map { (property, _) ->
                FieldMapping<K, Any?, Any?, Any?>(property, beanInfo.propertyDescriptors.find { it.name == property.name }!!, registry)
            }
            .toSet()

    private fun <K : Any> readTapMapping(clazz: KClass<K>, beanInfo: BeanInfo, registry: ConverterRegistry) =
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
