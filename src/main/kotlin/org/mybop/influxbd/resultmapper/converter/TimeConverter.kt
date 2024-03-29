package org.mybop.influxbd.resultmapper.converter

import java.util.concurrent.TimeUnit
import kotlin.reflect.KType

interface TimeConverter<T : Any?> {

    fun supportedType(): KType

    fun convert(instant: T): Long

    fun precision(): TimeUnit

    fun reverse(value: String): T
}
