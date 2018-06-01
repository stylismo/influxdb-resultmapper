# InfluxDB - Result Mapper

[![codebeat badge](https://codebeat.co/badges/acb0ebbb-ea83-46d8-a498-bb4e12784534)](https://codebeat.co/projects/github-com-gautierlevert-influxdb-resultmapper-develop)

[![Build Status](https://travis-ci.org/GautierLevert/influxdb-resultmapper.svg?branch=develop)](https://travis-ci.org/GautierLevert/influxdb-resultmapper)

How to use it
-------------

### Installation ###

This library is hosted in a personal Maven repository on Bintray : https://bintray.com/gautierlevert/maven/ormlite-rx 

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
compile 'org.mybop:ormlite-rx:INSERT_LAST_VERSION_HERE'
```

In Maven:

```xml
<dependency>
    <groupId>org.mybop</groupId>
    <artifactId>ormlite-rx</artifactId>
    <version>INSERT_LAST_VERSION_HERE</version>
    <type>pom</type>
</dependency>
```
