name                     := "standalone"
ThisBuild / organization := "org.codeminers"
ThisBuild / scalaVersion := "3.5.2"

// parsed by project/Versions.scala
val cpgVersion       = "1.7.26"
val joernVersion     = "4.0.250"
val flatgraphVersion = "0.1.9"

lazy val schema         = Projects.schema
lazy val domainClasses  = Projects.domainClasses
lazy val schemaExtender = Projects.schemaExtender

dependsOn(domainClasses)

libraryDependencies ++= Seq(
  "com.github.scopt"        %% "scopt"             % Versions.scopt,
  "org.apache.logging.log4j" % "log4j-slf4j2-impl" % Versions.log4j     % Optional,
  "io.joern"                %% "x2cpg"             % Versions.joern,
  "io.joern"                %% "javasrc2cpg"       % Versions.joern,
  "io.joern"                %% "semanticcpg"       % Versions.joern,
  "io.joern"                %% "dataflowengineoss" % Versions.joern,
  "io.joern"                %% "pysrc2cpg"         % Versions.joern,
  "io.joern"                %% "c2cpg"             % Versions.joern,
  "org.yaml"                 % "snakeyaml"          % Versions.snakeyaml
)

// Use the custom domain classes supplied by this build.
excludeDependencies ++= Seq(ExclusionRule("io.shiftleft", "codepropertygraph-domain-classes_3"))

assembly / assemblyMergeStrategy := {
  case "log4j2.xml"                                             => MergeStrategy.first
  case "module-info.class"                                      => MergeStrategy.first
  case "META-INF/versions/9/module-info.class"                  => MergeStrategy.first
  case "io/github/retronym/java9rtexport/Export.class"          => MergeStrategy.first
  case PathList("scala", "collection", "internal", "pprint", _) => MergeStrategy.first
  case x =>
    val oldStrategy = (ThisBuild / assemblyMergeStrategy).value
    oldStrategy(x)
}

ThisBuild / Compile / scalacOptions ++= Seq("-feature", "-deprecation", "-language:implicitConversions")

enablePlugins(JavaAppPackaging)

ThisBuild / licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / resolvers ++= Seq(
  Resolver.mavenLocal,
  "Sonatype OSS" at "https://oss.sonatype.org/content/repositories/public",
  "Atlassian" at "https://packages.atlassian.com/mvn/maven-atlassian-external",
  "Gradle Releases" at "https://repo.gradle.org/gradle/libs-releases/"
)

Compile / doc / sources                := Seq.empty
Compile / packageDoc / publishArtifact := false
