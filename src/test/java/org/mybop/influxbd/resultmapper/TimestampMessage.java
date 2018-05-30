package org.mybop.influxbd.resultmapper;

import java.util.Objects;

@Measurement(name = "message")
public class TimestampMessage {

    @Time(converter = TimestampMillisConverter.class)
    private final long timestamp;

    @Tag
    private final boolean important;

    @Field
    private String message;

    @Field
    private boolean dispatched = false;

    public TimestampMessage(final long timestamp, final boolean important) {
        this.timestamp = timestamp;
        this.important = important;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isImportant() {
        return important;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isDispatched() {
        return dispatched;
    }

    public void setDispatched(final boolean dispatched) {
        this.dispatched = dispatched;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimestampMessage)) {
            return false;
        }
        final TimestampMessage that = (TimestampMessage) o;
        return timestamp == that.timestamp &&
                important == that.important &&
                dispatched == that.dispatched &&
                Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, important, message, dispatched);
    }

    @Override
    public String toString() {
        return "TimestampMessage{" +
                "timestamp=" + timestamp +
                ", important=" + important +
                ", message='" + message + '\'' +
                ", dispatched=" + dispatched +
                '}';
    }
}
