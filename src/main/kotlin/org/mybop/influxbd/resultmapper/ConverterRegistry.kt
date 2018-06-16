package org.mybop.influxbd.resultmapper

import org.mybop.influxbd.resultmapper.converter.FieldConverter
import org.mybop.influxbd.resultmapper.converter.TagConverter
import org.mybop.influxbd.resultmapper.converter.TimeConverter
import org.mybop.influxbd.resultmapper.converter.basics.tag.BooleanConverter
import org.mybop.influxbd.resultmapper.converter.basics.tag.DoubleConverter
import org.mybop.influxbd.resultmapper.converter.basics.tag.EnumConverter
import org.mybop.influxbd.resultmapper.converter.basics.tag.FloatConverter
import org.mybop.influxbd.resultmapper.converter.basics.tag.IntegerConverter
import org.mybop.influxbd.resultmapper.converter.basics.tag.LongConverter
import org.mybop.influxbd.resultmapper.converter.basics.tag.StringConverter
import org.mybop.influxbd.resultmapper.converter.basics.time.InstantConverter
import org.mybop.influxbd.resultmapper.converter.basics.time.ZonedDateTimeConverter
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSupertypeOf

class ConverterRegistry {

    private val timeConverters = mutableSetOf<TimeConverter<out Any?>>()

    private val tagConverters = mutableSetOf<TagConverter<out Any?>>()

    private val fieldConverters = mutableSetOf<FieldConverter<out Any?, out Any?, out Any?>>()

    init {
        timeConverters.add(InstantConverter())
        timeConverters.add(ZonedDateTimeConverter())

        tagConverters.add(BooleanConverter())
        tagConverters.add(DoubleConverter())
        tagConverters.add(FloatConverter())
        tagConverters.add(IntegerConverter())
        tagConverters.add(LongConverter())
        tagConverters.add(StringConverter())
        tagConverters.add(EnumConverter())

        fieldConverters.add(org.mybop.influxbd.resultmapper.converter.basics.field.BooleanConverter())
        fieldConverters.add(org.mybop.influxbd.resultmapper.converter.basics.field.DoubleConverter())
        fieldConverters.add(org.mybop.influxbd.resultmapper.converter.basics.field.FloatConverter())
        fieldConverters.add(org.mybop.influxbd.resultmapper.converter.basics.field.IntegerConverter())
        fieldConverters.add(org.mybop.influxbd.resultmapper.converter.basics.field.LongConverter())
        fieldConverters.add(org.mybop.influxbd.resultmapper.converter.basics.field.StringConverter())
        fieldConverters.add(org.mybop.influxbd.resultmapper.converter.basics.field.EnumConverter())
    }

    fun registerTimeConverter(timeConverter: TimeConverter<*>) {
        timeConverters.add(timeConverter)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any?> findTimeConverterFor(type: KType): TimeConverter<T> =
            timeConverters.find {
                it.supportedType().isSupertypeOf(type)
            } as TimeConverter<T>?
                    ?: throw MappingException("No time converter found for type $type.")

    @Suppress("UNCHECKED_CAST")
    fun <T : Any?> findTimeConverter(converter: KClass<out TimeConverter<*>>): TimeConverter<T> =
            timeConverters.find {
                it::class == converter
            } as TimeConverter<T>?
                    ?: throw MappingException("No time converter with class $converter found.")

    fun registerTagConverter(tagConverter: TagConverter<*>) {
        tagConverters.add(tagConverter)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any?> findTagConverterFor(type: KType): TagConverter<T> =

            tagConverters.find {
                it.supportedType().isSupertypeOf(type)
            } as TagConverter<T>?
                    ?: throw MappingException("No tag converter found for type '$type'.")

    @Suppress("UNCHECKED_CAST")
    fun <T : Any?> findTagConverter(converter: KClass<out TagConverter<*>>): TagConverter<T> =
            tagConverters.find {
                it::class == converter
            } as TagConverter<T>?
                    ?: throw MappingException("No tag converter with class $converter found.")

    fun registerFieldConverter(fieldConverter: FieldConverter<*, *, *>) {
        fieldConverters.add(fieldConverter)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any?, D : Any?, R : Any?> findFieldConverterFor(type: KType): FieldConverter<T, D, R> =
            fieldConverters.find {
                it.supportedType().isSupertypeOf(type)
            } as FieldConverter<T, D, R>?
                    ?: throw MappingException("No field converter found for type $type.")

    @Suppress("UNCHECKED_CAST")
    fun <T : Any?, D : Any?, R : Any?> findFieldConverter(converter: KClass<out FieldConverter<*, *, *>>): FieldConverter<T, D, R> =
            fieldConverters.find {
                it::class == converter
            } as FieldConverter<T, D, R>?
                    ?: throw MappingException("No field converter with class $converter found.")
}
