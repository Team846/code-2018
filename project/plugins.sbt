resolvers += "Funky-Repo" at "http://team846.github.io/repo"

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.6")
addSbtPlugin("com.lynbrookrobotics" % "sbt-frc-jvm" % "0.5.0")
addSbtPlugin("com.lynbrookrobotics" % "sbt-frc-native" % "0.5.0" exclude("org.scala-native", "sbt-crossproject"))

addSbtPlugin("org.portable-scala" % "sbt-crossproject" % "0.3.0")
addSbtPlugin("org.scala-native" % "sbt-scala-native" % "0.3.7-arm-jni-threads" exclude("org.scala-native", "sbt-crossproject"))
