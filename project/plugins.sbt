resolvers += Resolver.typesafeRepo("releases")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.3")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.2")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.6.1")

addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.3.0")

// TODO: setup artifactory
//addSbtPlugin("org.scoverage" %% "sbt-scoverage" % "1.5.0")
//
//addSbtPlugin("org.scoverage" %% "sbt-coveralls" % "1.1.0")

// https://github.com/sbt/sbt/issues/1931
libraryDependencies += "org.slf4j" % "slf4j-nop" % "1.7.21"
