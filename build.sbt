name := "kafka_tdms"

version := "1.0"

scalaVersion := "2.10.5"

unmanagedBase := baseDirectory.value / "libs"

unmanagedJars in Compile := (baseDirectory.value  ** "*.jar").classpath

javaOptions in run += "-Djava.library.path=libs:lib/native/linux"

libraryDependencies ++= Seq (
  "org.apache.kafka" %% "kafka" % "0.8.1"
    exclude("javax.jms", "jms")
    exclude("com.sun.jdmk", "jmxtools")
    exclude("com.sun.jmx", "jmxri")
    exclude("org.slf4j", "slf4j-simple"),
  "ch.qos.logback" % "logback-classic" % "1.1.6",
  "ch.qos.logback" % "logback-core" % "1.1.6",
  "org.slf4j" % "slf4j-api" % "1.7.19",
  "net.liftweb" %% "lift-json" % "2.6.3",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test"
)
