package org.mybop.influxbd.resultmapper.mapping

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mybop.influxbd.resultmapper.ConverterRegistry
import org.mybop.influxbd.resultmapper.Foo
import java.time.Instant

class ClassMappingTest {
    @Test
    fun mapping() {
        val registry = ConverterRegistry()
        val mapping = ClassMapping.read(Foo::class, registry)
        System.out.print(mapping)

        val foo = Foo(
                Instant.EPOCH,
                "key",
                "value"
        )

        val point = mapping.toPoint(foo)

        assertThat(point.toString()).isEqualTo("Point [name=Foo, time=0, tags={key=key}, precision=MILLISECONDS, fields={bar=value}]")
    }
}
