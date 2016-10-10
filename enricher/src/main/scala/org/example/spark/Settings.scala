package org.example.spark

import java.time.temporal.ChronoUnit

import com.typesafe.config.ConfigFactory
import org.apache.spark.streaming.{Duration, Seconds}
import scala.collection.JavaConverters._

object Settings {
  private[this] val config = ConfigFactory.load()
  private[this] val appConfig =config.getConfig("avro-to-hdfs")

  val BatchDuration: Duration = Seconds(appConfig.getDuration("batch.duration").getSeconds)
  val KafkaTopic = appConfig.getString("topic")
  val Checkpoint = appConfig.getString("checkpoint")

  lazy val KafkaParams: Map[String, String] = {
    config.getConfig("kafka-consumer").entrySet().asScala.map({ entry =>
      entry.getKey -> entry.getValue.unwrapped().toString
    }).toMap
  }
}
