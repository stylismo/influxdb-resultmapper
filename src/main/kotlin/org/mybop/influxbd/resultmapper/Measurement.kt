package org.mybop.influxbd.resultmapper

/**
 * Annotation used on a class to be mapped as a measurement in influxDb request
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Measurement(
        val name: String = "",
        val retentionPolicy: String = "",
        val database: String = ""
)
