package org.mybop.influxbd.resultmapper

import java.time.Instant

@Measurement
data class Foo(
        @Time
        val time: Instant,
        @Tag
        val key: String,
        @Tag
        val strategy: Strategy,
        @Field
        val bar: String
)
