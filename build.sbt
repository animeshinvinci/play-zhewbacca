import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import sbt.Keys._

import scalariform.formatter.preferences._

val playFrameworkVersion = "2.7.1"

val commonSettings = Seq(
  organization := "org.zalando",
  version := playFrameworkVersion,
  scalaVersion := "2.12.8",
  // play 2.7.0 started to use scalac version 2.13.0-M5, but
  // we cannot use "2.13.0-M5" because of "atmos" library (it does not support scala 2.13 yet)
  crossScalaVersions := Seq("2.11.12", "2.12.8"),
  scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8", "-Xfatal-warnings"),
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value) {
      Some("snapshots" at nexus + "content/repositories/snapshots")
    }
    else {
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
    }
  },
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  resolvers := Seq(
    "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
    "scoverage-bintray" at "https://dl.bintray.com/sksamuel/sbt-plugins/"
  )
)

lazy val testDependencies =
  Seq(
    "org.specs2" %% "specs2-core" % "4.3.5" % "test",
    "org.specs2" %% "specs2-junit" % "4.3.5" % "test"
  )

lazy val playDependencies =
  Seq(
    "com.typesafe.play" %% "play-ahc-ws" % playFrameworkVersion,
    "com.typesafe.play" %% "play-json" % playFrameworkVersion,
    "com.typesafe.play" %% "play-ws" % playFrameworkVersion,
    "com.typesafe.play" %% "play" % playFrameworkVersion,
    "com.typesafe.play" %% "play-test" % playFrameworkVersion % "test",
    "com.typesafe.play" %% "play-specs2" % playFrameworkVersion % "test"
  )

lazy val libraries =
  Seq(
    "io.paradoxical" %% "atmos" % "2.2"
  )

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(name := "play-zhewbacca")
  .settings(libraryDependencies ++= (testDependencies ++ playDependencies ++ libraries))
  .settings(parallelExecution in Test := false)

// Define a special task which does not fail when any publish task fails for any module,
// so repeated publishing will be performed no matter the previous publish result.

// this checks violations to the paypal style guide
// copied from https://raw.githubusercontent.com/paypal/scala-style-guide/develop/scalastyle-config.xml
scalastyleConfig := file("scalastyle-config.xml")

scalastyleFailOnError := true

// Create a default Scala style task to run with tests
lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")

compileScalastyle := scalastyle.in(Compile).toTask("").value

(compileInputs in(Compile, compile)) := ((compileInputs in(Compile, compile)) dependsOn compileScalastyle).value

scalariformSettings(autoformat = true)

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(DoubleIndentConstructorArguments, true)
  .setPreference(PlaceScaladocAsterisksBeneathSecondAsterisk, true)
  .setPreference(PreserveSpaceBeforeArguments, false)
  .setPreference(AlignSingleLineCaseStatements, false)
  .setPreference(IndentLocalDefs, true)
  .setPreference(IndentPackageBlocks, true)
  .setPreference(IndentWithTabs, false)
  .setPreference(IndentSpaces, 2)
  .setPreference(MultilineScaladocCommentsStartOnFirstLine, false)
  .setPreference(SpacesAroundMultiImports, false)

pomExtra := (
  <url>https://github.com/zalando-stups/play-zhewbacca</url>
    <licenses>
      <license>
        <name>MIT License</name>
        <url>https://raw.githubusercontent.com/zalando-stups/play-zhewbacca/master/LICENSE</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:zalando-stups/play-zhewbacca.git</url>
      <connection>scm:git:git@github.com:zalando-stups/play-zhewbacca.git</connection>
    </scm>
    <developers>
      <developer>
        <name>Dmitry Krivaltsevich</name>
      </developer>
      <developer>
        <name>Mikhail Litvin</name>
      </developer>
    </developers>)
