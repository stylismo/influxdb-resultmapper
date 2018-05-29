package org.mybop.influxbd.resultmapper;

import java.util.Objects;

@Measurement(name = "message")
public class TimestampMessage {

    @Time(converter = TimestampMillisConverter.class)
    private long timestamp;

    @Field
    private String message;

    public TimestampMessage() {
    }

    public TimestampMessage(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimestampMessage that = (TimestampMessage) o;
        return timestamp == that.timestamp &&
                Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, message);
    }

    @Override
    public String toString() {
        return "TimestampMessage{" +
                "timestamp=" + timestamp +
                ", message='" + message + '\'' +
                '}';
    }
}
