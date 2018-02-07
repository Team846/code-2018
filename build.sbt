enablePlugins(FRCPlugin)

name := "code-2018"

version := "0.1"

scalaVersion := "2.12.4"

resolvers += "Funky-Repo" at "http://team846.github.io/repo"
resolvers += "WPILib-Maven" at "http://team846.github.io/wpilib-maven"
resolvers += "opencv-maven" at "http://first.wpi.edu/FRC/roborio/maven/development"

organization := "com.lynbrookrobotics"
teamNumber := 846

val potassiumVersion = "0.1.0-c691d7ba"
libraryDependencies += "com.lynbrookrobotics" %% "potassium-core" % potassiumVersion
libraryDependencies += "com.lynbrookrobotics" %% "potassium-commons" % potassiumVersion
libraryDependencies += "com.lynbrookrobotics" %% "potassium-frc" % potassiumVersion

val wpiVersion = "2018.1.1"
libraryDependencies += "edu.wpi.first" % "wpilib" % wpiVersion
libraryDependencies += "edu.wpi.first" % "cscore" % wpiVersion
libraryDependencies += "edu.wpi.first" % "ntcore" % wpiVersion
libraryDependencies += "edu.wpi.first" % "wpiutil" % wpiVersion
libraryDependencies += "com.ctre" % "phoenix" % "5.1.3.1"
libraryDependencies += "org.opencv" % "opencv-java" % "3.2.0"