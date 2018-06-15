package org.mybop.influxbd.resultmapper.mapping

internal interface PropertyMapping<K : Any, T : Any?, D : Any?, R : Any?, S : Any?> {

    val mappedName: String

    val propertyName: String

    fun extractField(value: K): D?

    fun parseResult(res: S): T

    fun writeField(obj: K, value: T)
}
