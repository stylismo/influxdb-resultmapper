package org.mybop.influxbd.resultmapper.mapping

import org.assertj.core.api.Assertions
import org.influxdb.dto.BoundParameterQuery
import org.junit.Test
import org.mybop.influxbd.resultmapper.*

class CustomConverterTest : DbTest() {

    @Test
    fun customTimeConverter() {
        val registry = ConverterRegistry()
        registry.registerTimeConverter(TimestampMillisConverter())

        val mapping = ClassMapping.read(TimestampMessage::class, registry)

        val timestampMessage = TimestampMessage(System.currentTimeMillis())
        timestampMessage.message = "super message"

        influxDB.write(database, retentionPolicy, mapping.toPoint(timestampMessage))

        val result = influxDB.query(
                BoundParameterQuery.QueryBuilder
                        .newQuery("SELECT * FROM \"$retentionPolicy\".\"message\"")
                        .forDatabase(database)
                        .create()
        )

        val parsed = mapping.parseQueryResult(result)
        Assertions.assertThat(parsed.size).isEqualTo(1)
        Assertions.assertThat(parsed[0].size).isEqualTo(1)

        val entry = parsed[0].entries.first()
        Assertions.assertThat(entry.key).isEqualTo(Key.EMPTY)
        Assertions.assertThat(entry.value.size).isEqualTo(1)

        val value = entry.value[0]
        Assertions.assertThat(value).isEqualTo(timestampMessage)

        System.out.println("$parsed")
    }
}
