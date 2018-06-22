package org.mybop.influxbd.resultmapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import java.time.Instant

class InfluxDaoTest : DbTest() {

    private lateinit var dao: InfluxDao<Foo>

    @Before
    fun setUpRegistry() {
        val registry = ConverterRegistry()
        dao = InfluxDao(Foo::class, registry, influxDB)
    }

    @Test
    fun simple() {
        val bar = Foo(
                Instant.now(),
                "tag1",
                Strategy.SIMPLE,
                null,
                "value",
                45
        )

        dao.save(bar)

        val values = dao.queryList("SELECT * FROM \"${dao.retentionPolicy}\".\"${dao.measurementName}\"")

        assertThat(values.size).isEqualTo(1)
        assertThat(values[0]).isEqualTo(bar)
    }

    @Test
    fun delete() {
        val bar = Foo(
                Instant.now(),
                "tag1",
                Strategy.SIMPLE,
                null,
                "value",
                45
        )

        dao.save(bar)

        dao.execute("DELETE FROM \"${dao.measurementName}\"")

        val values = dao.queryList("SELECT * FROM \"${dao.retentionPolicy}\".\"${dao.measurementName}\"")

        assertThat(values.size).isEqualTo(0)
    }
}
