import Dependencies._

lazy val commonSettings: Seq[Setting[_]] =
  Seq(
    scalaVersion := "2.11.8",
    libraryDependencies ++= Seq(logging.log4j, logging.slf4j, logging.slf4j_log4j12))


lazy val root = project
  .in(file("."))
  .settings(commonSettings)
  .disablePlugins(AssemblyPlugin)

lazy val producer = project.in(file("producer"))
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(kafka.client)
  )

lazy val enricher = project.in(file("enricher"))
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(spark.core, spark.streaming, spark.streamingKafka, typesafeConfig)
  )
