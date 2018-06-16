package org.mybop.influxbd.resultmapper.converter

import kotlin.reflect.KType

interface FieldConverter<T, D, R> {

    fun supportedType(): KType

    fun convert(value: T): D

    fun reverse(value: R, type: KType): T
}
