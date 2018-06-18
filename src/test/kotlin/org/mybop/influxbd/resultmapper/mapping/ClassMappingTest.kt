package org.mybop.influxbd.resultmapper.mapping

import org.assertj.core.api.Assertions.assertThat
import org.influxdb.dto.BoundParameterQuery
import org.junit.Test
import org.mybop.influxbd.resultmapper.ConverterRegistry
import org.mybop.influxbd.resultmapper.DbTest
import org.mybop.influxbd.resultmapper.Foo
import org.mybop.influxbd.resultmapper.Key
import org.mybop.influxbd.resultmapper.Strategy
import java.time.Instant


class ClassMappingTest : DbTest() {

    @Test
    fun mapping() {
        val (reader, writer) = ClassMappingIntrospector.mapper(Foo::class, ConverterRegistry())

        val foo = Foo(
                Instant.now(),
                "key",
                Strategy.COMPLETE,
                "opt",
                "value",
                32
        )

        val point = writer.toPoint(foo)

        influxDB.write(database, retentionPolicy, point)

        val result = influxDB.query(
                BoundParameterQuery.QueryBuilder
                        .newQuery("SELECT * FROM \"$retentionPolicy\".\"${writer.measurementName}\"")
                        .forDatabase(database)
                        .create()
        )

        val parsed = reader.parseQueryResult(result)
        assertThat(parsed.size).isEqualTo(1)
        assertThat(parsed[0].size).isEqualTo(1)

        val entry = parsed[0].entries.first()
        assertThat(entry.key).isEqualTo(Key.EMPTY)
        assertThat(entry.value.size).isEqualTo(1)

        val value = entry.value[0]
        assertThat(value).isEqualTo(foo)
    }
}
