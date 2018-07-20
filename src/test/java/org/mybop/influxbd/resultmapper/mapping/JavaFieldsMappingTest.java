package org.mybop.influxbd.resultmapper.mapping;

import org.influxdb.dto.BoundParameterQuery;
import org.influxdb.dto.QueryResult;
import org.junit.Test;
import org.mybop.influxbd.resultmapper.ClassWithFields;
import org.mybop.influxbd.resultmapper.ConverterRegistry;
import org.mybop.influxbd.resultmapper.DbTest;
import org.mybop.influxbd.resultmapper.Key;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class JavaFieldsMappingTest extends DbTest {

    @Test
    public void simpleMapping() {
        final ConverterRegistry registry = new ConverterRegistry();
        final ClassMappingIntrospector introspector = ClassMappingIntrospector.INSTANCE;

        final ClassReader<ClassWithFields> reader = introspector.reader(ClassWithFields.class, registry);
        final ClassWriter<ClassWithFields> writer = introspector.writer(ClassWithFields.class, registry);

        final ClassWithFields bar = new ClassWithFields(
                Instant.now(),
                "test",
                Collections.singletonMap("superField", "value")
        );

        getInfluxDB().write(getDatabase(), getRetentionPolicy(), writer.toPoint(bar));

        final QueryResult result = getInfluxDB().query(
                BoundParameterQuery.QueryBuilder
                        .newQuery("SELECT * FROM \"" + getRetentionPolicy() + "\".\"" + writer.getMeasurementName() + "\"")
                        .forDatabase(getDatabase())
                        .create()
        );

        final List<Map<Key, List<ClassWithFields>>> parsed = reader.parseQueryResult(result);
        assertThat(parsed.size()).isEqualTo(1);
        assertThat(parsed.get(0).size()).isEqualTo(1);

        final Map.Entry<Key, List<ClassWithFields>> entry = parsed.get(0).entrySet().iterator().next();
        assertThat(entry.getKey()).isEqualTo(Key.getEMPTY());
        assertThat(entry.getValue().size()).isEqualTo(1);

        final ClassWithFields value = entry.getValue().get(0);
        assertThat(value).isEqualTo(bar);
    }
}
