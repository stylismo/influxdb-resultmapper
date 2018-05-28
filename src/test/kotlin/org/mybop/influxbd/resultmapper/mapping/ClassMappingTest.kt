package org.mybop.influxbd.resultmapper.mapping

import org.assertj.core.api.Assertions.assertThat
import org.influxdb.InfluxDB
import org.influxdb.InfluxDBFactory
import org.influxdb.dto.BoundParameterQuery
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mybop.influxbd.resultmapper.ConverterRegistry
import org.mybop.influxbd.resultmapper.Foo
import org.mybop.influxbd.resultmapper.Key
import java.time.Instant


class ClassMappingTest {

    private lateinit var influxDB: InfluxDB

    private val database = "testDb"

    private val retentionPolicy = "testRp"

    @Before
    fun setUp() {
        influxDB = InfluxDBFactory.connect("http://localhost:8086", "user", "")
        influxDB.createDatabase(database)
        influxDB.createRetentionPolicy(retentionPolicy, database, "7d", "1d", 1)
    }

    @After
    fun tearDown() {
        influxDB.dropRetentionPolicy(database, retentionPolicy)
        influxDB.deleteDatabase(database)
        influxDB.close()
    }

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

        val point = mapping.toPoint(foo)

        influxDB.write(database, retentionPolicy, point)

        val result = influxDB.query(
                BoundParameterQuery.QueryBuilder
                        .newQuery("SELECT * FROM \"$retentionPolicy\".\"Foo\"")
                        .forDatabase(database)
                        .create()
        )

        val parsed = mapping.parseQueryResult(result)
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
