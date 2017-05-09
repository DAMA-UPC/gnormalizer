name := "gnormalizer"
organization := "edu.upc.dama"

version := "0.0.1"

scalaVersion := "2.12.2"

/*************   DEPENDENCIES   *************/

libraryDependencies ++= {
  val fs2Version = "0.9.5"
  val betterFilesVersion = "3.0.0"
  Seq(
    // https://github.com/functional-streams-for-scala/fs2
    "co.fs2" %% "fs2-core" % fs2Version,
    "co.fs2" %% "fs2-io" % fs2Version,
    // https://github.com/pathikrit/better-files
    "com.github.pathikrit" %% "better-files" % betterFilesVersion
  )
}

/**********  TEST DEPENDENCIES   ************/

libraryDependencies ++= {
  val specs2Version = "3.8.9"
  val scalaCheckVersion = "1.13.5"
  Seq(
    // Specs2 Test Framework - https://etorreborre.github.io/specs2/
    "org.specs2" %% "specs2-core" % specs2Version % "test",
    "org.specs2" %% "specs2-matcher-extra" % specs2Version % "test",
    "org.specs2" %% "specs2-junit" % specs2Version % "test",
    "org.specs2" %% "specs2-scalacheck" % specs2Version % "test",
    // ScalaCheck - https://scalacheck.org/
    "org.scalacheck" %% "scalacheck" % scalaCheckVersion % "test"
  )
}

/*************   TEST OPTIONS   *************/

scalacOptions in Test ++= Seq("-Yrangepos")

parallelExecution in Test := true

testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v", "-s", "-a")

/************    WartRemover    *************/

wartremoverErrors in (Compile, compile) ++= {
  Warts.allBut(
    Wart.MutableDataStructures, // We need them due the application performance requirements.
    Wart.Var, // We need them due the application performance requirements.
    Wart.NonUnitStatements, // Mutable Scala collections APIs always return Unit values.
    Wart.PublicInference,
    Wart.DefaultArguments,
    Wart.FinalCaseClass
  )
}
wartremoverErrors in (Test, test) ++= Warts.allBut(
  Wart.FinalCaseClass,
  Wart.NonUnitStatements,
  Wart.PublicInference
)

/*************    Scapegoat    **************/

scapegoatVersion := "1.3.0"

import com.sksamuel.scapegoat.sbt.ScapegoatSbtPlugin.autoImport.scapegoat
(test in Test) := ((test in Test) dependsOn scapegoat).value

/************    ScalaStyle    **************/

scalastyleFailOnError := true

lazy val testScalastyle = taskKey[Unit]("testScalastyle")
testScalastyle := org.scalastyle.sbt.ScalastylePlugin.scalastyle.in(Test).toTask("").value
(test in Test) := ((test in Test) dependsOn testScalastyle).value
