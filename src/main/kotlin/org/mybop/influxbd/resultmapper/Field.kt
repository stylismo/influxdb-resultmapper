package org.mybop.influxbd.resultmapper

import org.mybop.influxbd.resultmapper.converter.FieldConverter
import kotlin.reflect.KClass

/**
 * Annotation used on a field to be mapped as a field in influxDb request
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
annotation class Field(
        val name: String = "",
        val converter: KClass<out FieldConverter<*, *>> = FieldConverter::class
)
