package org.mybop.influxbd.resultmapper

import java.time.Instant

@Measurement(database = "testDb", retentionPolicy = "testRp")
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
