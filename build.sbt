name := "gnormalizer"

version := "0.0.1"

scalaVersion := "2.11.8"

// Dependencies
libraryDependencies ++= {
  val fs2Version = "0.9.1"
  Seq(
    // https://github.com/functional-streams-for-scala/fs2
    "co.fs2" %% "fs2-core" % fs2Version,
    "co.fs2" %% "fs2-io" % fs2Version
  )
}

// Test Dependencies
resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases" // Nedeed for Scalamock

libraryDependencies ++= {
  val specs2Version = "3.8.5"
  val specs2ScalaMockVersion = "3.2.2"
  val scalaCheckVersion = "1.13.2"
  Seq(
    // Specs2 Test Framework - https://etorreborre.github.io/specs2/website/SPECS2-3.8.5/quickstart.html
    "org.specs2" %% "specs2-core" % specs2Version % "test",
    "org.specs2" %% "specs2-matcher-extra" % specs2Version % "test",
    "org.specs2" %% "specs2-junit" % specs2Version % "test",
    "org.specs2" %% "specs2-cats" % specs2Version % "test",
    "org.scalamock" %% "scalamock-specs2-support" % specs2ScalaMockVersion % "test",
    "org.scalacheck" %% "scalacheck" % scalaCheckVersion % "test"
  )
}

// Test Options
scalacOptions in Test ++= Seq("-Yrangepos")

parallelExecution in Test := true

testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v", "-s", "-a")
