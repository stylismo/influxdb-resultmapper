package org.mybop.influxbd.resultmapper;

import java.time.ZonedDateTime;
import java.util.Objects;

@Measurement(database = "testDb", name = "measurement_bar")
public class Bar {

    @Time
    private ZonedDateTime createdAt;

    @Tag
    private String zone;

    @Tag
    private Category category;

    @Field(name = "count")
    private int number;

    @Field
    private Color color;

    public Bar() {
    }

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

    public Category getCategory() {
        return category;
    }

    public void setCategory(final Category category) {
        this.category = category;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(final Color color) {
        this.color = color;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Bar)) {
            return false;
        }
        final Bar bar = (Bar) o;
        return number == bar.number &&
                createdAt.isEqual(bar.createdAt) &&
                Objects.equals(zone, bar.zone) &&
                category == bar.category &&
                color == bar.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(createdAt, zone, category, number, color);
    }

    @Override
    public String toString() {
        return "Bar{" +
                "createdAt=" + createdAt +
                ", zone='" + zone + '\'' +
                ", category=" + category +
                ", number=" + number +
                ", color=" + color +
                '}';
    }
}
