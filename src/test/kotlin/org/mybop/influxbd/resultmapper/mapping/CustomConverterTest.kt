package org.mybop.influxbd.resultmapper.mapping

import org.assertj.core.api.Assertions
import org.influxdb.dto.BoundParameterQuery
import org.junit.Test
import org.mybop.influxbd.resultmapper.ClassMappingIntrospector
import org.mybop.influxbd.resultmapper.ConverterRegistry
import org.mybop.influxbd.resultmapper.DbTest
import org.mybop.influxbd.resultmapper.Key
import org.mybop.influxbd.resultmapper.TimestampMessage
import org.mybop.influxbd.resultmapper.TimestampMillisConverter

class CustomConverterTest : DbTest() {

    @Test
    fun customTimeConverter() {
        val registry = ConverterRegistry()
        registry.registerTimeConverter(TimestampMillisConverter())

        val (reader, writer) = ClassMappingIntrospector(registry).mapper(TimestampMessage::class)

        val timestampMessage = TimestampMessage(System.currentTimeMillis(), true)
        timestampMessage.message = "super message"

        influxDB.write(database, retentionPolicy, writer.toPoint(timestampMessage))

        val result = influxDB.query(
                BoundParameterQuery.QueryBuilder
                        .newQuery("SELECT * FROM \"$retentionPolicy\".\"${writer.measurementName}\"")
                        .forDatabase(database)
                        .create()
        )

        val parsed = reader.parseQueryResult(result)
        Assertions.assertThat(parsed.size).isEqualTo(1)
        Assertions.assertThat(parsed[0].size).isEqualTo(1)

        val entry = parsed[0].entries.first()
        Assertions.assertThat(entry.key).isEqualTo(Key.EMPTY)
        Assertions.assertThat(entry.value.size).isEqualTo(1)

        val value = entry.value[0]
        Assertions.assertThat(value).isEqualTo(timestampMessage)
    }
}
