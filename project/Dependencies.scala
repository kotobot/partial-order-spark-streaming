import sbt._

object Dependencies {
  object logging {
    val log4j = "log4j" % "log4j" % "1.2.17"
    val slf4j = "org.slf4j" % "slf4j-parent" % "1.7.21"
    val slf4j_log4j12 = "org.slf4j" % "slf4j-log4j12" % "1.7.21"
  }

  object kafka {
    val client = "org.apache.kafka" % "kafka-clients" % "0.10.0.1"
  }

  object spark {
    val version = "2.0.1"
    val core = "org.apache.spark" %% "spark-core" % version
    val streaming = "org.apache.spark" %% "spark-streaming" % version
    val streamingKafka = "org.apache.spark" %% "spark-streaming-kafka-0-10" % version
  }

  val typesafeConfig = "com.typesafe" % "config" % "1.3.0"
}