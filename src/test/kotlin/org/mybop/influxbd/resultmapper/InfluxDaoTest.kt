package org.mybop.influxbd.resultmapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import java.time.Instant

class InfluxDaoTest : DbTest() {

    private lateinit var registry: ConverterRegistry

    private lateinit var introspector: ClassMappingIntrospector

    private lateinit var dao: InfluxDao<Foo>

    @Before
    fun setUpRegistry() {
        registry = ConverterRegistry()
        introspector = ClassMappingIntrospector(registry)
        dao = InfluxDao(Foo::class, introspector, influxDB)
    }

    @Test
    fun simple() {
        val bar = Foo(
                Instant.now(),
                "tag1",
                Strategy.SIMPLE,
                "value"
        )

        dao.save(bar)

        val values = dao.queryList("SELECT * FROM \"${dao.retentionPolicy}\".\"${dao.measurementName}\"")

        assertThat(values.size).isEqualTo(1)
        assertThat(values[0]).isEqualTo(bar)
    }
}
