# InfluxDB - Result Mapper

[![codebeat badge](https://codebeat.co/badges/acb0ebbb-ea83-46d8-a498-bb4e12784534)](https://codebeat.co/projects/github-com-gautierlevert-influxdb-resultmapper-develop)

[![Build Status](https://travis-ci.org/GautierLevert/influxdb-resultmapper.svg?branch=develop)](https://travis-ci.org/GautierLevert/influxdb-resultmapper)

[![Download](https://api.bintray.com/packages/gautierlevert/maven/influxdb-resultmapper/images/download.svg)](https://bintray.com/gautierlevert/maven/influxdb-resultmapper/_latestVersion)

# What is it ?

This library goal is to provide basic dao features for Java projects using InfluxDB.

Want more ? What about a query builder : [influxdb-querybuilder](https://github.com/GautierLevert/influxdb-querybuilder) ;)

## InfluxDb

InfluxDb is a time series database meant to store large amounts of timestamped data : [official website](https://www.influxdata.com/time-series-platform/influxdb/)

It is a part of the TICK stack : [ossicial website](https://www.influxdata.com/time-series-platform/)

This library should work with open source and commercial version.

This library parses queries made by the [official java client](https://github.com/influxdata/influxdb-java) and has no intention to replace it.

## Kotlin ?

This library is built in Kotlin but it is meant to be fully functional in JDK 8 (thanks to the [language built-in compatibility](https://kotlinlang.org/docs/reference/java-to-kotlin-interop.html))

If you're looking for more information about this language you can check the [official website](https://kotlinlang.org/)

## Example

Here is an example of code (extracted from the unit tests) :

### Kotlin

```kotlin 
@Measurement(database = "testDb", retentionPolicy = "testRp")
data class Foo(
        @Time
        val time: Instant,
        @Tag
        val key: String,
        @Tag
        val strategy: Strategy,
        @Field
        val bar: String
)

val bar = Foo(
    Instant.now(),
    "tag1",
    Strategy.SIMPLE,
    "value"
)

dao.save(bar)

val values = dao.queryList("SELECT * FROM \"${dao.retentionPolicy}\".\"${dao.measurementName}\"")
```

### Java

```java
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
    
    ...
}

final Bar bar = new Bar();
bar.setCreatedAt(ZonedDateTime.now());
bar.setColor(Color.RED);
bar.setCategory(Category.B);
bar.setNumber(43);
bar.setZone("abc");

dao.save(bar);

final List<Bar> values = dao.queryList("SELECT * FROM \"" + dao.getMeasurementName() + "\"");
```

# How to use it

## Installation

This library is hosted in a personal Maven repository on Bintray : https://bintray.com/gautierlevert/maven/influxdb-resultmapper

You need to add this repository to your build script.

In Gradle:

```groovy
repositories {
    maven {
        url "http://dl.bintray.com/gautierlevert/maven"
    }
}
```

In Maven:

```xml
<repositories>
    <repository>
        <id>bintray-gautierlevert-maven</id>
        <name>bintray</name>
        <url>http://dl.bintray.com/gautierlevert/maven</url>
    </repository>
</repositories>
```

And then add the library dependency :


In Gradle:

```groovy
compile 'org.mybop:influxdb-resultmapper:INSERT_LAST_VERSION_HERE'
```

In Maven:

```xml
<dependency>
    <groupId>org.mybop</groupId>
    <artifactId>influxdb-resultmapper</artifactId>
    <version>INSERT_LAST_VERSION_HERE</version>
    <type>pom</type>
</dependency>
```

## Usage

Main classes are :
 - `org.mybop.influxbd.resultmapper.ConverterRegistry`: A dictionary of all known converters used to parse and read fields.
 - `org.mybop.influxbd.resultmapper.InfluxDao`: A basic data object that can be used to write points and parse queries.

### Converter Registry

The registry contains a set of converter (for tag, field and time).

Basic converters are provided for basic types (for tags and fields) :
 - boolean
 - numbers : int, long, float, double (primitives and Wrappers)
 - String
 - enum
 
And for java time following classes (for time column) :
 - Instant
 - ZonedDateTime (__NB: timezone is lost during conversion__)
 
You can add your own converters by calling `register...()` methods.

### Dao

This class owns the basic methods `save()` and `query()`

#### Create Dao

Kotlin:
```kotlin
val dao = InfluxDao(Foo::class, registry, influxDB)
```
Java:
```java
InfluxDao<Bar> dao = new InfluxDao<>(Bar.class, registry, getInfluxDB());
```

### Using Dao

Kotlin:
```kotlin 
dao.save(bar)

val values = dao.queryList("SELECT * FROM \"${dao.retentionPolicy}\".\"${dao.measurementName}\"")
```

Java:
```java
dao.save(bar);

final List<Bar> values = dao.queryList("SELECT * FROM \"" + dao.getMeasurementName() + "\"");
```
