# business-index-data

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/45803fabda2c4fac8aaf3bc081cbf129)](https://www.codacy.com/app/ONSDigital/business-index-data?utm_source=github.com&utm_medium=referral&utm_content=ONSdigital/business-index-data&utm_campaign=badger)

Set of applications responsible for processing input files with business data and produce business index (in elasticsearch)
It's available to run as a standalone application or via Apache Spark (TBD).

It's expect 4 input files:
* Company Houses (csv)
* HMRC PAYE information (csv)
* HMRC VAT information (csv)
* Linking file (json)

For each links provided in JSON - application generate separate business index record if at least one record for specific company found (either VAT, PAYE or CH)

### Prerequisites

* Java 8 or higher
* SBT (http://www.scala-sbt.org/)

### Development Setup (MacOS)

To install/run ElasticSearch on MacOS, use Homebrew (http://brew.sh):

- `brew install homebrew/versions/elasticsearch24`
- `elasticsearch`

The last command runs an interactive Elasticsearch 2.4.1 session that the application can connect to using cluster name
`elasticsearch_<your username>`.

### Running

To compile, build and run the application (by default it will connect to your local ElasticSearch):

```shell
sbt run -Denvironment=local
```

To package the project in a runnable fat-jar:

```shell
sbt assembly
```
