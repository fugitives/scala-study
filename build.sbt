name := "scala-study"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies ++= {
  val scalaTestVersion = "3.2.2"
  Seq(
    "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
    "org.scalatest" %% "scalatest-mustmatchers" % scalaTestVersion % "test",
    "org.scalatest" %% "scalatest-diagrams" % scalaTestVersion % "test",
    "org.scalatestplus" %% "scalacheck-1-14" % "3.2.2.0" % "test"
  )
}