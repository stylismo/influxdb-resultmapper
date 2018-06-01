package org.mybop.influxbd.resultmapper;

import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class JavaInfluxDaoTest extends DbTest {

    private ConverterRegistry registry;

    private ClassMappingIntrospector introspector;

    private InfluxDao<Bar> dao;

    @Before
    public void setUpRegistry() {
        registry = new ConverterRegistry();
        introspector = new ClassMappingIntrospector(registry);
        dao = new InfluxDao<>(Bar.class, introspector, getInfluxDB());
    }

    @Test
    public void simple() {
        final Bar bar = new Bar();
        bar.setCreatedAt(ZonedDateTime.now());
        bar.setColor(Color.RED);
        bar.setCategory(Category.B);
        bar.setNumber(43);
        bar.setZone("abc");

        dao.save(bar);

        final List<Bar> values = dao.queryList("SELECT * FROM \"" + dao.getMeasurementName() + "\"");

        assertThat(values.size()).isEqualTo(1);
        assertThat(values.get(0)).isEqualTo(bar);
    }
}