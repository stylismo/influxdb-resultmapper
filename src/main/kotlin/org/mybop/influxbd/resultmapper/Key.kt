package org.mybop.influxbd.resultmapper

data class Key(
        private val value: Map<String, Any?> = mapOf()
) {
    companion object {
        val EMPTY = Key(emptyMap())
    }
}
