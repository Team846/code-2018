resolvers += "Funky-Repo" at "http://lynbrookrobotics.com/repo"

addSbtPlugin("com.lynbrookrobotics" % "sbt-frc-jvm" % "0.5.5")
addSbtPlugin(
  "com.lynbrookrobotics" % "sbt-frc-native" % "0.5.5" exclude ("org.scala-native", "sbt-crossproject")
)

addSbtPlugin("org.portable-scala" % "sbt-crossproject" % "0.3.0")
addSbtPlugin(
  "org.scala-native" % "sbt-scala-native" % "0.3.7-arm-jni-threads" exclude ("org.scala-native", "sbt-crossproject")
)

addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.5.10")
