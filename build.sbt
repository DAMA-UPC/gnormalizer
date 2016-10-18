name := "gnormalizer"

version := "0.0.1"

scalaVersion := "2.11.8"

// Dependencies
libraryDependencies ++= {
  val fs2Version = "0.9.1"
  val betterFilesVersion = "2.16.0"
  Seq(
    // https://github.com/functional-streams-for-scala/fs2
    "co.fs2" %% "fs2-core" % fs2Version,
    "co.fs2" %% "fs2-io" % fs2Version,
    // https://github.com/pathikrit/better-files
    "com.github.pathikrit" %% "better-files" % betterFilesVersion
  )
}

// Test Dependencies
resolvers += "ScalaMock Repository" at "http://dl.bintray.com/scalaz/releases"

libraryDependencies ++= {
  val specs2Version = "3.8.5"
  val specs2ScalaMockVersion = "3.3.0"
  val scalaCheckVersion = "1.13.2"
  Seq(
    // Specs2 Test Framework - https://etorreborre.github.io/specs2/
    "org.specs2" %% "specs2-core" % specs2Version % "test",
    "org.specs2" %% "specs2-matcher-extra" % specs2Version % "test",
    "org.specs2" %% "specs2-junit" % specs2Version % "test",
    "org.specs2" %% "specs2-scalacheck" % specs2Version % "test",
    // Scalamock - http://scalamock.org/
    "org.scalamock" %% "scalamock-specs2-support" % specs2ScalaMockVersion % "test",
    // ScalaCheck - https://scalacheck.org/
    "org.scalacheck" %% "scalacheck" % scalaCheckVersion % "test"
  )
}

// Static Code Analysis tools options
wartremoverErrors ++= {
  Warts.allBut(
    Wart.DefaultArguments, // TODO: Move the defaults to configuration files.
    Wart.MutableDataStructures, // We need them due the application performance requirements.
    Wart.Var, // We need them due the application performance requirements.
    Wart.NonUnitStatements // BetterFiles and Scala mutable collections APIs always return values.
  )
}

// Test Options
scalacOptions in Test ++= Seq("-Yrangepos")

parallelExecution in Test := true

testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v", "-s", "-a")

// Create a default Scala style task to run with tests
lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")
compileScalastyle := org.scalastyle.sbt.ScalastylePlugin.scalastyle.in(Compile).toTask("").value
(compile in Compile) <<= (compile in Compile) dependsOn compileScalastyle

lazy val testScalastyle = taskKey[Unit]("testScalastyle")
testScalastyle := org.scalastyle.sbt.ScalastylePlugin.scalastyle.in(Test).toTask("").value
(test in Test) <<= (test in Test) dependsOn testScalastyle
