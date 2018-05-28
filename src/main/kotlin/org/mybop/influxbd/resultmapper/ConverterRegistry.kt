package org.mybop.influxbd.resultmapper

import org.mybop.influxbd.resultmapper.converter.FieldConverter
import org.mybop.influxbd.resultmapper.converter.TagConverter
import org.mybop.influxbd.resultmapper.converter.TimeConverter
import org.mybop.influxbd.resultmapper.converter.basics.tag.BooleanConverter
import org.mybop.influxbd.resultmapper.converter.basics.tag.DoubleConverter
import org.mybop.influxbd.resultmapper.converter.basics.tag.FloatConverter
import org.mybop.influxbd.resultmapper.converter.basics.tag.IntegerConverter
import org.mybop.influxbd.resultmapper.converter.basics.tag.LongConverter
import org.mybop.influxbd.resultmapper.converter.basics.tag.StringConverter
import org.mybop.influxbd.resultmapper.converter.basics.time.InstantConverter
import org.mybop.influxbd.resultmapper.converter.basics.time.ZonedDateTimeConverter
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSupertypeOf

internal class ConverterRegistry {

    private val timeConverters = mutableSetOf<TimeConverter<*>>()

    private val tagConverters = mutableSetOf<TagConverter<*>>()

    private val fieldConverters = mutableSetOf<FieldConverter<*, *>>()

    init {
        timeConverters.add(InstantConverter())
        timeConverters.add(ZonedDateTimeConverter())

        tagConverters.add(BooleanConverter())
        tagConverters.add(DoubleConverter())
        tagConverters.add(FloatConverter())
        tagConverters.add(IntegerConverter())
        tagConverters.add(LongConverter())
        tagConverters.add(StringConverter())

        fieldConverters.add(org.mybop.influxbd.resultmapper.converter.basics.field.BooleanConverter())
        fieldConverters.add(org.mybop.influxbd.resultmapper.converter.basics.field.DoubleConverter())
        fieldConverters.add(org.mybop.influxbd.resultmapper.converter.basics.field.FloatConverter())
        fieldConverters.add(org.mybop.influxbd.resultmapper.converter.basics.field.IntegerConverter())
        fieldConverters.add(org.mybop.influxbd.resultmapper.converter.basics.field.LongConverter())
        fieldConverters.add(org.mybop.influxbd.resultmapper.converter.basics.field.StringConverter())
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> findTimeConverterFor(type: KType): TimeConverter<T> =
            timeConverters.find {
                it.supportedType().isSupertypeOf(type)
            } as TimeConverter<T>?
                    ?: throw MappingException("No time converter found for type $type.")

    @Suppress("UNCHECKED_CAST")
    fun <T> findTimeConverter(converter: KClass<out TimeConverter<*>>): TimeConverter<T> =
            timeConverters.find {
                it::class == converter
            } as TimeConverter<T>?
                    ?: throw MappingException("No time converter with class $converter found.")

    @Suppress("UNCHECKED_CAST")
    fun <T> findTagConverterFor(type: KType): TagConverter<T> =
            tagConverters.find {
                it.supportedType().isSupertypeOf(type)
            } as TagConverter<T>?
                    ?: throw MappingException("No tag converter found for type $type.")

    @Suppress("UNCHECKED_CAST")
    fun <T> findTagConverter(converter: KClass<out TagConverter<*>>): TagConverter<T> =
            tagConverters.find {
                it::class == converter
            } as TagConverter<T>?
                    ?: throw MappingException("No tag converter with class $converter found.")

    @Suppress("UNCHECKED_CAST")
    fun <T, R> findFieldConverterFor(type: KType): FieldConverter<T, R> =
            fieldConverters.find {
                it.supportedType().isSupertypeOf(type)
            } as FieldConverter<T, R>?
                    ?: throw MappingException("No field converter found for type $type.")

    @Suppress("UNCHECKED_CAST")
    fun <T, R> findFieldConverter(converter: KClass<out FieldConverter<*, *>>): FieldConverter<T, R> =
            fieldConverters.find {
                it::class == converter
            } as FieldConverter<T, R>?
                    ?: throw MappingException("No field converter with class $converter found.")
}
