package org.mybop.influxbd.resultmapper;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

@Measurement
public class ClassWithFields {

    @Time
    private Instant timestamp;

    @Tag
    private String tag;

    @Fields
    private Map<String, Object> fields;

    public ClassWithFields(final Instant timestamp, final String tag, final Map<String, Object> fields) {
        this.timestamp = timestamp;
        this.tag = tag;
        this.fields = fields;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getTag() {
        return tag;
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClassWithFields)) {
            return false;
        }
        final ClassWithFields that = (ClassWithFields) o;
        return Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(tag, that.tag) &&
                Objects.equals(fields, that.fields);
    }

    @Override
    public int hashCode() {

        return Objects.hash(timestamp, tag, fields);
    }

    @Override
    public String toString() {
        return "ClassWithFields{" +
                "timestamp=" + timestamp +
                ", tag='" + tag + '\'' +
                ", fields=" + fields +
                '}';
    }
}
