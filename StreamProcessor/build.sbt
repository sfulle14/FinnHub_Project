//Optional TO DO: verify configs
lazy val root = (project in file(".")).
    settings(
        name := "steamprocessor",
        version := "1.0",
        scalaVersion := "2.12.15"
    )

val sparkVersion = "3.0.0"

// some of these libraries below are unnecessary
// TODO: verify
libraryDependencies ++= Seq(
    "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
    "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",
    "org.apache.spark" %% "spark-avro" % sparkVersion % "provided",
    "org.apache.spark" %% "spark-sql-kafka-0-10" % sparkVersion % "provided",
    "com.datastax.spark" %% "spark-cassandra-connector" % sparkVersion % "provided",
    "com.datastax.cassandra" %% "cassandra-drive-connector" % "3.11.3" % "provided",
    "com.typesafe" % "config" % "1.4.1"
)

javaOptions := Seq("-Dconfig.resource=deployment.conf")

// include filename for readability
assemblyJarName in assembly := "streamprocessor-assembly-1-0.jar"

assemblyMergeStrategy in assembly := {
    case "reference.conf" => MergeStrategy.concat
    case "META-INF/services/org.apache.spark.sql.sources.DataSourceRegiser" => MergeStrategy.concat
    case PathList("META-INF", xs@_*) => MergeStrategy.discord
    case _ => MergeStrategy.first
}
