scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.scalariform" %% "scalariform" % "0.1.7",
  "org.scalaz" %% "scalaz-core" % "7.1.4",
  "org.scalaz" %% "scalaz-effect" % "7.1.4",
  "com.lihaoyi" %% "ammonite-ops" % "0.4.8",
  "com.twitter" %% "util-eval" % "6.27.0",
  "com.github.scopt" %% "scopt" % "3.3.0",
  "org.specs2" %% "specs2-core" % "3.6.4" % "test")