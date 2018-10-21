// -------- EXTRA REPOSITORIES --------- //
resolvers += "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/"

// ----------- SBT PLUGINS -------------- //
// https://github.com/sbt/sbt-assembly
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.8")
// https://github.com/sbt/sbt-release
addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.9")
// https://github.com/sbt/sbt-native-packager
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.10")
// https://github.com/rtimush/sbt-updates
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.3.4")
// https://github.com/scoverage/sbt-scoverage
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")
// https://github.com/codacy/sbt-codacy-coverage
addSbtPlugin("com.codacy" % "sbt-codacy-coverage" % "1.3.15")
// https://github.com/puffnfresh/wartremover
addSbtPlugin("org.wartremover" % "sbt-wartremover" % "2.3.7")
// https://github.com/coursier/coursier
addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.2")
// https://github.com/sbt/sbt-bintray
addSbtPlugin("org.foundweekends" % "sbt-bintray" % "0.5.4")

// ------------- SBT OPTIONS ----------- //
logLevel := Level.Warn
