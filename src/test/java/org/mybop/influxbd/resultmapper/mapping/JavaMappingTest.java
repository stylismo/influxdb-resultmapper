package org.mybop.influxbd.resultmapper.mapping;

import org.influxdb.dto.BoundParameterQuery;
import org.influxdb.dto.QueryResult;
import org.junit.Test;
import org.mybop.influxbd.resultmapper.Bar;
import org.mybop.influxbd.resultmapper.Category;
import org.mybop.influxbd.resultmapper.ClassMappingIntrospector;
import org.mybop.influxbd.resultmapper.Color;
import org.mybop.influxbd.resultmapper.ConverterRegistry;
import org.mybop.influxbd.resultmapper.DbTest;
import org.mybop.influxbd.resultmapper.Key;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class JavaMappingTest extends DbTest {

    @Test
    public void simpleMapping() {
        final ConverterRegistry registry = new ConverterRegistry();
        final ClassMappingIntrospector mapping = new ClassMappingIntrospector(registry);

        final ClassReader<Bar> reader = mapping.reader(Bar.class);
        final ClassWriter<Bar> writer = mapping.writer(Bar.class);

        final Bar bar = new Bar();
        bar.setCreatedAt(ZonedDateTime.now());
        bar.setCategory(Category.B);
        bar.setNumber(12);
        bar.setColor(Color.BLUE);
        bar.setZone("pt1");

        getInfluxDB().write(getDatabase(), getRetentionPolicy(), writer.toPoint(bar));

        final QueryResult result = getInfluxDB().query(
                BoundParameterQuery.QueryBuilder
                        .newQuery("SELECT * FROM \"" + getRetentionPolicy() + "\".\"" + writer.getMeasurementName() + "\"")
                        .forDatabase(getDatabase())
                        .create()
        );

        final List<Map<Key, List<Bar>>> parsed = reader.parseQueryResult(result);
        assertThat(parsed.size()).isEqualTo(1);
        assertThat(parsed.get(0).size()).isEqualTo(1);

        final Map.Entry<Key, List<Bar>> entry = parsed.get(0).entrySet().iterator().next();
        assertThat(entry.getKey()).isEqualTo(Key.getEMPTY());
        assertThat(entry.getValue().size()).isEqualTo(1);

        final Bar value = entry.getValue().get(0);
        assertThat(value).isEqualTo(bar);
    }
}
