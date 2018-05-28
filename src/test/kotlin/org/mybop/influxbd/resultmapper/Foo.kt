package org.mybop.influxbd.resultmapper

import java.time.Instant

@Measurement
data class Foo(
        @Time
        val time: Instant,
        @Tag
        val key: String,
        @Field
        val bar: String
)
