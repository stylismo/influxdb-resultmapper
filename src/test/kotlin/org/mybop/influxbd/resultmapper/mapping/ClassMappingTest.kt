package org.mybop.influxbd.resultmapper.mapping

import org.assertj.core.api.Assertions.assertThat
import org.influxdb.dto.BoundParameterQuery
import org.junit.Test
import org.mybop.influxbd.resultmapper.ConverterRegistry
import org.mybop.influxbd.resultmapper.DbTest
import org.mybop.influxbd.resultmapper.Foo
import org.mybop.influxbd.resultmapper.Key
import java.time.Instant


class ClassMappingTest : DbTest() {

    @Test
    fun mapping() {
        val registry = ConverterRegistry()
        val mapping = ClassMapping.read(Foo::class, registry)
        System.out.print(mapping)

        val foo = Foo(
                Instant.now(),
                "key",
                "value"
        )

        val point = mapping.writer.toPoint(foo)

        influxDB.write(database, retentionPolicy, point)

        val result = influxDB.query(
                BoundParameterQuery.QueryBuilder
                        .newQuery("SELECT * FROM \"$retentionPolicy\".\"${mapping.writer.measurementName}\"")
                        .forDatabase(database)
                        .create()
        )

        val parsed = mapping.reader.parseQueryResult(result)
        assertThat(parsed.size).isEqualTo(1)
        assertThat(parsed[0].size).isEqualTo(1)

        val entry = parsed[0].entries.first()
        assertThat(entry.key).isEqualTo(Key.EMPTY)
        assertThat(entry.value.size).isEqualTo(1)

        val value = entry.value[0]
        assertThat(value).isEqualTo(foo)

        System.out.println("$parsed")
    }
}
