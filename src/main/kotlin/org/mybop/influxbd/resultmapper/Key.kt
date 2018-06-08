package org.mybop.influxbd.resultmapper

data class Key internal constructor(
        val value: Map<String, Any?> = mapOf()
) {
    companion object {
        @JvmStatic
        val EMPTY = Key(emptyMap())
    }
}
