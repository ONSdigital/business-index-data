lazy val Versions = new {
  val util = "0.28.3"
  val elastic4s = "2.3.1"
  val spark = "1.6.0"
  val elasticSearchSpark = "2.4.0"
  val json4s = "3.5.0"
  val scallop = "0.9.5"
  val joda = "2.9.4"
  val jodaConvert = "1.8.1"
}

lazy val commonSettings = Seq(
  scalaVersion := "2.11.8",
  // artifactory:  scapegoatVersion := "1.1.0",

  // next properties set required for sbt-assembly plugin,
  // whe it finds two classes with same name in different JARs it does not know what to do
  // we're defining merge strategy for problematic classes (mostly it's spark deps)
  assemblyMergeStrategy in assembly := {
    case PathList("javax", "servlet", xs@_*) => MergeStrategy.last
    case PathList("javax", "activation", xs@_*) => MergeStrategy.last
    case PathList("org", "apache", xs@_*) => MergeStrategy.last
    case PathList("org", "slf4j", xs@_*) => MergeStrategy.last
    case PathList("org", "joda", xs@_*) => MergeStrategy.last
    case PathList("com", "google", xs@_*) => MergeStrategy.last
    case PathList("com", "esotericsoftware", xs@_*) => MergeStrategy.last
    case PathList("com", "codahale", xs@_*) => MergeStrategy.last
    case PathList("com", "yammer", xs@_*) => MergeStrategy.last
    case "about.html" => MergeStrategy.rename
    case "META-INF/ECLIPSEF.RSA" => MergeStrategy.last
    case "META-INF/mailcap" => MergeStrategy.last
    case "META-INF/mimetypes.default" => MergeStrategy.last
    case "plugin.properties" => MergeStrategy.last
    case "log4j.properties" => MergeStrategy.last
    case x =>
      val oldStrategy = (assemblyMergeStrategy in assembly).value
      oldStrategy(x)
  },
  resolvers ++= Seq(
    "splunk" at "http://splunk.artifactoryonline.com/splunk/ext-releases-local"
  ),
  scalacOptions in ThisBuild ++= Seq(
    "-encoding", "UTF-8",
    "-language:reflectiveCalls",
    "-language:experimental.macros",
    "-language:implicitConversions",
    "-language:higherKinds",
    "-language:postfixOps",
    "-deprecation", // warning and location for usages of deprecated APIs
    "-feature", // warning and location for usages of features that should be imported explicitly
    "-unchecked", // additional warnings where generated code depends on assumptions
    "-Xlint", // recommended additional warnings
    "-Xcheckinit", // runtime error when a val is not initialized due to trait hierarchies (instead of NPE somewhere else)
    "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver
    //"-Yno-adapted-args", // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver
    "-Ywarn-value-discard", // Warn when non-Unit expression results are unused
    "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures
    "-Ywarn-dead-code", // Warn when dead code is identified
    "-Ywarn-numeric-widen" // Warn when numerics are widened
  )
)

lazy val businessIndex = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    name := "business-index-data",
    moduleName := "business-index-data"
  ).aggregate(
  biUtils,
  biIngestion,
  biBulkMatch
)

lazy val biIngestion = (project in file("bi-ingestion"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "org.rogach" %% "scallop" % Versions.scallop
      // ,
      //      "org.apache.spark" %% "spark-core" % Versions.spark,
      //      "org.elasticsearch" %% "elasticsearch-spark" % Versions.elasticSearchSpark excludeAll {
      //        ExclusionRule(organization = "javax.servlet")
      //      }
    )
  ).dependsOn(
  biUtils
)

lazy val biBulkMatch = (project in file("bi-bulk-match"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "org.scalaj" %% "scalaj-http" % "2.3.0",
      "javax.mail" % "mail" % "1.4"
    )
  ).dependsOn(
  biUtils
)

// pure library without any dependencies to elasticsearch or spark
// in future suppose to be reused in API project
lazy val biUtils = (project in file("bi-utils"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe" % "config" % "1.3.1",
      "com.sksamuel.elastic4s" %% "elastic4s-streams" % Versions.elastic4s exclude("org.scalactic", "scalactic_2.11"),
      "joda-time" % "joda-time" % Versions.joda,
      "org.json4s" %% "json4s-native" % Versions.json4s,
      "org.scalatest" %% "scalatest" % "3.0.0" % Test,
      "org.slf4j" % "slf4j-api" % "1.7.22",
      "ch.qos.logback" % "logback-classic" % "1.1.7"
    )
  )

