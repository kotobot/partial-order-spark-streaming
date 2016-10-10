package org.example.spark

import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.{HashPartitioner, SparkConf}
import org.example.spark.Settings._

object Enricher {

  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf()
    val ssc = new StreamingContext(sparkConf, Seconds(5))

    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferBrokers,
      ConsumerStrategies.Subscribe[String, String](Seq(KafkaTopic), KafkaParams))

    //fault-tolerant implementation should load initial state from store
    val emptyRDD = ssc.sparkContext.emptyRDD[(String, TsAndNum)]

    val stateSpec = StateSpec.function(trackStateFunc _)
      .initialState(emptyRDD)
      .partitioner(new HashPartitioner(10)) //TODO: get number of partitions from Kafka topic (and use adapter to Kafka's partitioner?)
      .timeout(Seconds(60 + 60)) //user session expiration + grace period for late events

    val enrichedStream =
      stream
        .map(cr => cr.key() -> (cr.timestamp(), cr.value()))
        .mapPartitions(sort, preservePartitioning = true)
        .mapWithState(stateSpec)

    //ssc.checkpoint(Checkpoint)
    ssc.start()
    ssc.awaitTermination()
    ssc.stop(stopSparkContext = true, stopGracefully = true)
  }

  def trackStateFunc(batchTime: Time, key: String, value: Option[(Long, String)], state: State[TsAndNum]): Option[String] = {
    val (ts, v) = value.getOrElse(0L -> "")
    val lastState = state.getOption.getOrElse(TsAndNum(ts, -1))
    val newState =
      if (ts - lastState.timestamp <= 60 * 1000) TsAndNum(ts, lastState.num + 1)
      else TsAndNum(ts, 0)
    state.update(newState)
    Some(s"$v, ${newState.num}, ${newState.timestamp}")
  }

  //sort by timestamp to ensure correct event ordering, sorting algorithm should be stable
  //and efficient for almost ordered sequences
  def sort(p: Iterator[(String, (Long, String))]): Iterator[(String, (Long, String))] = {
    p.toSeq.sortBy({case (k, (ts, v)) => k -> ts}).iterator //naive implementation
  }

}

case class TsAndNum(timestamp: Long, num: Int)
