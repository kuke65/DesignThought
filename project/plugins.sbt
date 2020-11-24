addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.9")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.21")
addSbtPlugin("io.spray" % "sbt-revolver" % "0.9.0")
addSbtPlugin("se.marcuslonnberg" % "sbt-docker" % "1.5.0")
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.9.0")
addSbtPlugin("com.eed3si9n" % "sbt-projectmatrix" % "0.5.0")
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.32")

// addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.5.0-RC2")

addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler-sjs06" % "0.16.0")
addSbtPlugin("org.olegych" %% "sbt-cached-ci" % "1.0.3")

//workaround https://github.com/sbt/sbt/issues/5374
allExcludeDependencies ++= List(
  ExclusionRule().withOrganization("org.webjars").withName("envjs"),
  ExclusionRule().withOrganization("com.google.javascript").withName("closure-compiler-externs")
)