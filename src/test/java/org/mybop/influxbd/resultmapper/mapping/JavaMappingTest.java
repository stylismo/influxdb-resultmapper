package org.mybop.influxbd.resultmapper.mapping;

import org.influxdb.dto.BoundParameterQuery;
import org.influxdb.dto.QueryResult;
import org.junit.Test;
import org.mybop.influxbd.resultmapper.Bar;
import org.mybop.influxbd.resultmapper.ConverterRegistry;
import org.mybop.influxbd.resultmapper.DbTest;
import org.mybop.influxbd.resultmapper.Key;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public class JavaMappingTest extends DbTest {

    @Test
    public void simpleMapping() throws IntrospectionException {
        final ConverterRegistry registry = new ConverterRegistry();
        final ClassMapping<Bar> mapping = ClassMapping.Companion.read(Bar.class, registry);

        final Bar bar = new Bar();
        bar.setCreatedAt(ZonedDateTime.now());
        bar.setNumber(12);
        bar.setZone("pt1");

        getInfluxDB().write(getDatabase(), getRetentionPolicy(), mapping.toPoint(bar));

        final QueryResult result = getInfluxDB().query(
                BoundParameterQuery.QueryBuilder
                        .newQuery("SELECT * FROM \"" + getRetentionPolicy() + "\".\"measurement_bar\"")
                        .forDatabase(getDatabase())
                        .create()
        );

        List<Map<Key, List<Bar>>> parsed = mapping.parseQueryResult(result);

        System.out.println(parsed);
    }
}
