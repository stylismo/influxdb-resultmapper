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
        @Tag
        val optional: String?,
        @Field
        val bar: String,
        @Field
        val number: Int
)
