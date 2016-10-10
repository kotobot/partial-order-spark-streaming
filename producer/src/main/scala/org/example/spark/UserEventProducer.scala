package org.example.spark

import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}

object UserEventProducer {

  val NumOfEvents = 100
  val NumOfUsers = 7
  val Topic = "test"

  private[this] val kafkaProducer = {
    //TODO: move kafka config to application.conf
    val props = new Properties()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:6667")
    props.put(ProducerConfig.ACKS_CONFIG, "all")
    props.put(ProducerConfig.BATCH_SIZE_CONFIG, "16384")
    props.put(ProducerConfig.LINGER_MS_CONFIG, "5")
    props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, "33554432")
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroSerializer")
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroSerializer")
    new KafkaProducer[String, String](props)
  }

  def main(args: Array[String]): Unit = {
    for (i <- 0 until NumOfEvents) {
      kafkaProducer.send(
        new ProducerRecord[String, String](Topic, userId(i), eventBody(i))
      )
    }
  }

  def userId(i: Int): String = "user-" + (i % NumOfUsers)

  def eventBody(i: Int): String = s"click-$i"

}