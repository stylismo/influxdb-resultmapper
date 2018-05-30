package org.mybop.influxbd.resultmapper.mapping;

import org.influxdb.dto.BoundParameterQuery;
import org.influxdb.dto.QueryResult;
import org.junit.Test;
import org.mybop.influxbd.resultmapper.Bar;
import org.mybop.influxbd.resultmapper.Category;
import org.mybop.influxbd.resultmapper.Color;
import org.mybop.influxbd.resultmapper.ConverterRegistry;
import org.mybop.influxbd.resultmapper.DbTest;
import org.mybop.influxbd.resultmapper.Key;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public class JavaMappingTest extends DbTest {

    @Test
    public void simpleMapping() {
        final ConverterRegistry registry = new ConverterRegistry();
        final ClassMapping<Bar> mapping = ClassMapping.read(Bar.class, registry);

        final Bar bar = new Bar();
        bar.setCreatedAt(ZonedDateTime.now());
        bar.setCategory(Category.B);
        bar.setNumber(12);
        bar.setColor(Color.BLUE);
        bar.setZone("pt1");

        getInfluxDB().write(getDatabase(), getRetentionPolicy(), mapping.getWriter().toPoint(bar));

        final QueryResult result = getInfluxDB().query(
                BoundParameterQuery.QueryBuilder
                        .newQuery("SELECT * FROM \"" + getRetentionPolicy() + "\".\"" + mapping.getWriter().getMeasurementName() + "\"")
                        .forDatabase(getDatabase())
                        .create()
        );

        List<Map<Key, List<Bar>>> parsed = mapping.getReader().parseQueryResult(result);

        System.out.println(parsed);
    }
}
