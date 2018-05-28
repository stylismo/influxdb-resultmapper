package org.mybop.influxbd.resultmapper.converter

import kotlin.reflect.KType

interface TagConverter<T : Any?> {

    fun supportedType(): KType

    fun convert(key: T): String?

    fun reverse(key: String?): T
}
