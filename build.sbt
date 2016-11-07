
lazy val commonSettings = Seq(
	name := "business-index-data",
	version := "0.1.0",
	scalaVersion := "2.10.6",
	organisation := "uk.gov.ons",
	resolvers ++= Seq(
	  // allows us to include spark packages
	  Resolver.typesafeRepo("releases"),
	  Resolver.sonatypeRepo("releases"),
	  Resolver.bintrayRepo("spark-packages", "maven")
	),
	scalacOptions ++= Seq(
		"-feature",
		"-deprecation",
		"-unchecked",
		"-Xlint"
	)
)

lazy val Versions = new {
	val spark = "1.6.0"
	val sparkCsv = "1.5.0"
	val scalatest = "3.0.0"
	val typesafeConfig = "1.3.1"
	val elasticsearchSpark = "2.4.0"
}

lazy val businessIndex = (project in file("."))
	.settings(commonSettings: _*)
	.settings(
		libraryDependencies ++= Seq(
  		"org.apache.spark" %% "spark-core" % Versions.spark,
		  "org.apache.spark" %% "spark-sql" % Versions.spark,
		  "com.databricks" %% "spark-csv" % Versions.sparkCsv,
		  "com.typesafe" % "config" % Versions.typesafeConfig,
		  "org.elasticsearch" %% "elasticsearch-spark" % Versions.elasticsearchSpark,
		  "org.scalatest" %% "scalatest" % Versions.scalatest % Test
		)
	)



