package org.mybop.influxbd.resultmapper

import org.mybop.influxbd.resultmapper.converter.TimeConverter
import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
annotation class Time(
        val converter: KClass<out TimeConverter<*>> = TimeConverter::class
)
