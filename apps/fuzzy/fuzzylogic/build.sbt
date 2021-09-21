//import Dependencies._

ThisBuild / scalaVersion     := "2.12.10"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"
lazy val root = (project in file("."))
  .settings(
    name := "fuzzylogic",
    libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.1.2"
    //libraryDependencies += "com.eed3si9n" % "sbt-assembly" % "0.14.8",
    // libraryDependencies += scalaTest % Test,
    // libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.1.1" % "compile",
    // libraryDependencies += "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.1.1" % Test,
    // libraryDependencies += "org.apache.spark" %% "spark-streaming" % "3.1.1" % "provided",
    // libraryDependencies += "com.acme.common" % "commonclass" % "1.0" % "compile" from "file:///home/spark/spark-3.1.1-bin-hadoop2.7/examples/jars/fuzzylib_2.12-0.1.jar",
    //target in assembly := new File("/home/spark/spark-3.1.1-bin-hadoop2.7/jars/"),
    //target in assembly := file("/home/spark/spark-3.1.1-bin-hadoop2.7/jars/"),
    //assemblyOutputPath in assembly := new File("/target/package.jar")
  )