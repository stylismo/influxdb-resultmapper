package org.mybop.influxbd.resultmapper.mapping

internal interface PropertyMapping<K : Any, T : Any?, R : Any?, S : Any?> {

    val mappedName: String

    val propertyName: String

    fun extractField(value: K): R?

    fun parseResult(res: S): T

    fun writeField(obj: K, value: T)
}
