package org.mybop.influxbd.resultmapper

import org.influxdb.InfluxDB
import org.influxdb.InfluxDBFactory
import org.junit.After
import org.junit.Before

open class DbTest {

    protected lateinit var influxDB: InfluxDB
        private set

    protected val database = "testDb"

    protected val retentionPolicy = "testRp"

    @Before
    fun setUp() {
        influxDB = InfluxDBFactory.connect("http://localhost:8086", "user", "")
        influxDB.createDatabase(database)
        influxDB.createRetentionPolicy(retentionPolicy, database, "7d", "1d", 1)
        println("InfluxDb ${influxDB.version()}")
    }

    @After
    fun tearDown() {
        influxDB.dropRetentionPolicy(database, retentionPolicy)
        influxDB.deleteDatabase(database)
        influxDB.close()
    }
}
