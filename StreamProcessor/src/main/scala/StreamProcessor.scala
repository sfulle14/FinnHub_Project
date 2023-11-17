// this is the main Spark app responsible for stream processing
// it retrieves messages from Kafka, transforms it in Spark engine and loads into Cassandra.

import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.avro.functions._ 
import org.apache.spark.sql.streaming._ 
import org.apache.spark.sql.types._ 
import org.apache.spark.sql.cassandra._ 

import com.datastax.oss.driver.api.core.uuid.Uuids
import com.datastax.spark.connector._ 

import com.typesafe.config.{Config, ConfigFactory}

import scala.io.Source

object StreamProcessor {
    def main(args:Array[String]): Unit = 
    {
        //loading configuration
        val conf: Config = ConfigFactory.load()
        val settings: Settings = new Settings(conf)

        //loading trades schema
        val tradesSchema:; String = Source.fromInputStream(
            getClass.getResourceAsStream(settings.schemas("trades"))).mkString

        //udf for Cassandra Uuids
        val makeUUID = udf(() => Uuids.timeBased().toString())

        //create Spark session
        val spark = SparkSession
            .builder
            .master(settings.spark("master"))
            .appName(settings.spark("appName"))
            .config("spark.cassandra.connection.host", settings.cassandra("host"))
            .config("spark.cassandra.connection.host", settings.cassandra("host"))
            .config("spark.cassandra.auth.username", settings.cassandra("username"))
            .config("spark.cassandra.auth.password", settings.cassandra("password"))
            .config("spark.sql.shuffle.partitions", settings.spark("shuffle_partitions"))
            .getOrCreate()

        //proper processing code 
        import spark.implicits._ 

        //read streams from Kafka

        //explode the data from avro

        //rename columns and add proper timestamps

        //write query to Cassandra

        //another datafram with aggregates, running averages from last 15 schemas

        //rename columns in data fram and add UUIDs before inserting to Cassandra

        //write second query to Cassandra

        //let query await termination
        spark.streams.awaitAnyTermination()
    }
}
