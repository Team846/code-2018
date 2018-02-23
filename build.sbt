import scala.sys.process.Process

name := "code-2018"

version := "0.1"

organization := "com.lynbrookrobotics"

resolvers in ThisBuild += "Funky-Repo" at "http://lynbrookrobotics.com/repo"
resolvers in ThisBuild += "WPILib-Maven" at "http://lynbrookrobotics.com/wpilib-maven"
resolvers in ThisBuild += "opencv-maven" at "http://first.wpi.edu/FRC/roborio/maven/development"

val potassiumVersion = "0.1.0-fc6457a8"
val wpiVersion = "2018.2.2"

lazy val robot = crossProject(JVMPlatform, NativePlatform)
  .crossType(CrossType.Full)
  .in(file("."))
  .settings(
    libraryDependencies += "com.lynbrookrobotics" %%% "potassium-core" % potassiumVersion,
    libraryDependencies += "com.lynbrookrobotics" %%% "potassium-commons" % potassiumVersion,
    libraryDependencies += "com.lynbrookrobotics" %%% "potassium-frc" % potassiumVersion,
    libraryDependencies += "com.lynbrookrobotics" %%% "potassium-config" % potassiumVersion,
    libraryDependencies += "com.lynbrookrobotics" %%% "funky-dashboard" % "0.3.0"
  )
  .jvmSettings(
    scalaVersion := "2.12.4",
    libraryDependencies += "edu.wpi.first" % "wpilib" % wpiVersion,
    libraryDependencies += "edu.wpi.first" % "cscore" % wpiVersion,
    libraryDependencies += "edu.wpi.first" % "ntcore" % wpiVersion,
    libraryDependencies += "edu.wpi.first" % "wpiutil" % wpiVersion,
    libraryDependencies += "com.ctre" % "phoenix" % "5.1.3.1",
    libraryDependencies += "org.opencv" % "opencv-java" % "3.2.0"
  )
  .nativeSettings(
    libraryDependencies += "com.lynbrookrobotics" %%% "wpilib-scala-native" % "0.1.2",
    libraryDependencies += "com.lynbrookrobotics" %%% "ntcore-scala-native" % "0.1.2",
    libraryDependencies += "com.lynbrookrobotics" %%% "phoenix-scala-native" % "0.1.2",
    scalaVersion := "2.11.12",
    scalacOptions ++= Seq("-target:jvm-1.8")
  )

lazy val jvm = robot.jvm
  .enablePlugins(FRCPluginJVM)
  .settings(
    teamNumber := 846,
    scalacOptions ++= Seq(
      "-Ywarn-unused-import",
      "-Ywarn-unused:locals",
      "-Ywarn-unused:privates",
      "-Ywarn-adapted-args"
    )
  )

val boehmFolder = file("cross-compile/bdwgc")
val libunwindFolder = file("cross-compile/libunwind")
val librtFolder = file("cross-compile/re2")

import scala.scalanative.sbtplugin.ScalaNativePluginInternal._
import scala.scalanative.sbtplugin.Utilities._

val crossCompileSettings = Seq(
  // fork to link with gcc instead of clang
  nativeLinkLL in Compile := {
    val linked = (nativeLinkNIR in Compile).value
    val logger = streams.value.log
    val apppaths = (nativeCompileLL in Compile).value
    val nativelib = (nativeCompileLib in Compile).value
    val cwd = (nativeWorkdir in Compile).value
    val target = nativeTarget.value
    val gc = nativeGC.value
    val linkingOpts = nativeLinkingOptions.value
    //val clangpp = nativeClangPP.value
    val outpath = (artifactPath in nativeLink in Compile).value

    val links = {
      val os = target.split("-")(2) //Option(sys props "os.name").getOrElse("")
      val arch = target.split("-").head
      // we need re2 to link the re2 c wrapper (cre2.h)
      val librt = Seq.empty // we want to statically link librt
      val libunwind = Seq.empty //Seq("unwind", "unwind-" + arch) we want to statically link libunwind

      librt ++ libunwind ++ linked.links.map(_.name) // ++ garbageCollector(gc).links
    }.filterNot(_ == "re2")

    val linkopts = links.map("-l" + _) ++ linkingOpts
    val targetopt = Seq("-target", target)
    val flags = Seq("-o", outpath.abs) ++ linkopts // ++ targetopt
    // statically link libunwind
    val opaths = ((nativelib ** "*.o").get.map(_.abs) ++
      (file("custom-c") ** "*.o").get.map(_.abs)) :+
      (libunwindFolder / "lib" / "libunwind.a").abs :+
      (libunwindFolder / "lib" / "libunwind-arm.a").abs :+
      (librtFolder / "lib" / "libre2.a").abs :+
      (boehmFolder / "gc.a").abs

    val paths = apppaths.map(_.abs) ++ opaths
    val compile = "arm-frc-linux-gnueabi-gcc" +: (flags ++ paths)

    logger.time("Linking native code") {
      logger.running(compile)
      Process(compile, cwd) ! logger
    }

    outpath
  },
  nativeTarget := "arm-frc-linux-gnueabi",
  nativeCompileOptions ++= Seq(
    "-funwind-tables",
    "-target",
    "armv7a-frc-linux-gnueabi",
    "-mfpu=neon",
    "-mfloat-abi=soft",
    s"--sysroot=${CrossSettings.toolchainPath.abs}",
    s"-I${(libunwindFolder / "include").abs}",
    s"-I${(librtFolder / "include").abs}",
    s"-I${(boehmFolder / "include").abs}",
    s"-I${CrossSettings.toolchainPath.abs}/include/c++/5.5.0",
    s"-I${CrossSettings.toolchainPath.abs}/include/c++/5.5.0/arm-frc-linux-gnueabi",
    s"-I${(baseDirectory.value / "../cross-compile/allwpilib/wpilibj/src/arm-linux-jni").abs}",
    s"-I${(baseDirectory.value / "../cross-compile/allwpilib/wpilibj/src/arm-linux-jni/linux").abs}"
  ),
  nativeLinkingOptions ++= Seq(
    "-lm",
    "-lc",
    "-lstdc++",
    "-lpthread",
    "-ldl", // system stuff,
    // transitive dependencies
    "-lwpilibJNI",
    "-lntcore",
    "-lCTRE_PhoenixCCI",
    "-lwpiHal",
    "-lwpiutil",
    "-l:libniriosession.so.17.0.0",
    "-l:libniriodevenum.so.17.0.0",
    "-l:libRoboRIO_FRC_ChipObject.so.18.0.0",
    "-l:libvisa.so",
    "-l:libFRC_NetworkCommunication.so.18.0.0",
    "-l:libNiFpga.so.17.0.0",
    "-l:libNiFpgaLv.so.17.0.0",
    "-l:libNiRioSrv.so.17.0.0",
    s"-L${(baseDirectory.value / "../cross-compile/wpilib-core/lib/linux/athena/shared").abs}",
    s"-L${(baseDirectory.value / "../cross-compile/phoenix/java/lib").abs}",
    s"-L${(baseDirectory.value / "../cross-compile/wpilib-cpp/reflib/linux/athena/shared").abs}"
  )
)

lazy val native = robot.native
  .enablePlugins(ScalaNativePlugin, FRCPluginNative)
  .settings(
    teamNumber := 846,
    nativeMode := "debug",
    nativeGC := "boehm",
    crossCompileSettings
  )
