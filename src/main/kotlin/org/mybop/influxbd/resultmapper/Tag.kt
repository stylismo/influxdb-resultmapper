package org.mybop.influxbd.resultmapper

import org.mybop.influxbd.resultmapper.converter.TagConverter
import kotlin.reflect.KClass

/**
 * Annotation used on a field to be mapped as a tag in influxDb request
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
annotation class Tag(
        val name: String = "",
        val converter: KClass<out TagConverter<*>> = TagConverter::class
)
