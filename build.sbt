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
  resolvers ++= Seq(
    Resolver.bintrayRepo("outworkers", "oss-releases"),
    "splunk" at "http://splunk.artifactoryonline.com/splunk/ext-releases-local"
  ),
  scalacOptions in ThisBuild ++= Seq(
    "-encoding","UTF-8",
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
    models,
    sparkIngestion
  ).enablePlugins(CrossPerProjectPlugin)

lazy val sparkIngestion = (project in file("ingestion"))
  .settings(commonSettings: _*)
  .settings(
    crossScalaVersions := Seq("2.10.6"),
    libraryDependencies ++= Seq(
      "org.json4s" %% "json4s-native" % Versions.json4s,
      "org.rogach" %% "scallop" % Versions.scallop,
      "com.sksamuel.elastic4s" %% "elastic4s-streams" % Versions.elastic4s,
      "org.apache.spark" %% "spark-core" % Versions.spark,
      "org.elasticsearch" %% "elasticsearch-spark" % Versions.elasticSearchSpark excludeAll {
        ExclusionRule(organization = "javax.servlet")
      },
      "com.outworkers" %% "util-testing" % Versions.util % Test
    )
  ).dependsOn(
    models
  ).enablePlugins(CrossPerProjectPlugin)

lazy val models = (project in file("models"))
  .settings(commonSettings: _*)
  .settings(
    crossScalaVersions := Seq("2.10.6", "2.11.8"),
    libraryDependencies ++= Seq(
      "joda-time" %  "joda-time" % Versions.joda,
      "org.json4s" %% "json4s-native" % Versions.json4s,
      "com.outworkers" %% "util-parsers-cats" % Versions.util,
      "com.outworkers" %% "util-validators-cats" % Versions.util,
      "com.outworkers" %% "util-testing" % Versions.util % Test
    )
  ).enablePlugins(CrossPerProjectPlugin)