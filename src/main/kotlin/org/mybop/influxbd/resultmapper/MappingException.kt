package org.mybop.influxbd.resultmapper

class MappingException(
        message: String,
        cause: Throwable? = null
) : RuntimeException(message, cause)
