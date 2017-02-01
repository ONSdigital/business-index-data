lazy val Versions = new {
  val phantom = "2.1.1"
  val util = "0.27.8"
  val elastic4s = "2.3.1"
  val spark = "1.6.0"
  val elasticSearchSpark = "2.4.0"
}

lazy val commonSettings = Seq(
  scalaVersion := "2.11.8",
  resolvers ++= Seq(
    Resolver.bintrayRepo("outworkers", "oss-releases"),
    "splunk" at "http://splunk.artifactoryonline.com/splunk/ext-releases-local"
  ),
  scalacOptions in ThisBuild ++= Seq(
    "-language:experimental.macros",
    "-target:jvm-1.8",
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
    "-Ywarn-unused", // Warn when local and private vals, vars, defs, and types are unused
    "-Ywarn-unused-import", //  Warn when imports are unused (don't want IntelliJ to do it automatically)
    "-Ywarn-numeric-widen" // Warn when numerics are widened
  )
)

lazy val businessIndex = (project in file("."))
	.settings(commonSettings: _*)
	.settings(
		libraryDependencies ++= Seq(
			"org.rogach" %% "scallop" % "0.9.5",
			"com.sksamuel.elastic4s" %% "elastic4s-streams" % Versions.elastic4s,
			"org.apache.spark" %% "spark-core" % Versions.spark,
			"org.elasticsearch" %% "elasticsearch-spark" % Versions.elasticSearchSpark excludeAll {
				ExclusionRule(organization = "javax.servlet")
			},
			"com.outworkers" %% "util-testing" % Versions.util % Test
		)
	)
