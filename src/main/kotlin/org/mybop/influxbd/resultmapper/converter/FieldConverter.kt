package org.mybop.influxbd.resultmapper.converter

import kotlin.reflect.KType

interface FieldConverter<T, R> {

    fun supportedType(): KType

    fun convert(value: T): R

    fun reverse(value: R): T
}
