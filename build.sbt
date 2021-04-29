name := "analyzer"

version := "0.1"

scalaVersion := "2.12.10"

libraryDependencies ++= Seq(
  "org.apache.flink" %% "flink-scala" % "1.12.1",
  "org.apache.flink" %% "flink-clients" % "1.12.1",
  "org.apache.flink" %% "flink-streaming-scala" % "1.12.1",
  "org.apache.flink" %% "flink-connector-kafka" % "1.12.1",
  "org.mongodb.scala" %% "mongo-scala-driver" % "2.7.0",
  "com.jsoniter" % "jsoniter" % "0.9.23",
  "org.slf4j" % "slf4j-log4j12" % "1.7.30",
  "org.scalatest" %% "scalatest" % "3.1.0" % "test",
)

resolvers in Global ++= Seq(
  "Sbt plugins" at "https://dl.bintray.com/sbt/sbt-plugin-releases",
  "Cloudera Repository" at "https://repository.cloudera.com/artifactory/cloudera-repos/"
)
