package org.mybop.influxbd.resultmapper;

import java.time.ZonedDateTime;
import java.util.Objects;

@Measurement(name = "measurement_bar")
public class Bar {

    @Time
    private ZonedDateTime createdAt;

    @Tag
    private String zone;

    @Field(name = "count")
    private int number;

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bar bar = (Bar) o;
        return number == bar.number &&
                Objects.equals(createdAt, bar.createdAt) &&
                Objects.equals(zone, bar.zone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(createdAt, zone, number);
    }

    @Override
    public String toString() {
        return "Bar{" +
                "createdAt=" + createdAt +
                ", zone='" + zone + '\'' +
                ", number=" + number +
                '}';
    }
}
