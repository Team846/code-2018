import scala.sys.process.Process

name := "code-2018"

version := "0.1"

organization := "com.lynbrookrobotics"

resolvers in ThisBuild += "Funky-Repo" at "http://lynbrookrobotics.com/repo"
resolvers in ThisBuild += "WPILib-Maven" at "http://lynbrookrobotics.com/wpilib-maven"
resolvers in ThisBuild += "opencv-maven" at "http://first.wpi.edu/FRC/roborio/maven/development"

val potassiumVersion = "0.1.0-cc17fccd"
val wpiVersion = "2018.2.2"

lazy val robot = crossProject(JVMPlatform, NativePlatform).crossType(CrossType.Full).in(file(".")).settings(
  libraryDependencies += "com.lynbrookrobotics" %%% "potassium-core" % potassiumVersion,
  libraryDependencies += "com.lynbrookrobotics" %%% "potassium-commons" % potassiumVersion,
  libraryDependencies += "com.lynbrookrobotics" %%% "potassium-frc" % potassiumVersion,
  libraryDependencies += "com.lynbrookrobotics" %%% "potassium-config" % potassiumVersion,
  libraryDependencies += "com.chuusai" %%% "shapeless" % "2.3.3",
  libraryDependencies += "io.argonaut" %%% "argonaut" % "6.2.1",
  libraryDependencies += "com.github.alexarchambault" %%% "argonaut-shapeless_6.2" % "1.2.0-M8",
  libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value
).jvmSettings(
  scalaVersion := "2.12.4",
  libraryDependencies += "edu.wpi.first" % "wpilib" % wpiVersion,
  libraryDependencies += "edu.wpi.first" % "cscore" % wpiVersion,
  libraryDependencies += "edu.wpi.first" % "ntcore" % wpiVersion,
  libraryDependencies += "edu.wpi.first" % "wpiutil" % wpiVersion,
  libraryDependencies += "com.ctre" % "phoenix" % "5.1.3.1",
  libraryDependencies += "org.opencv" % "opencv-java" % "3.2.0"
).nativeSettings(
  libraryDependencies += "com.lynbrookrobotics" %%% "wpilib-scala-native" % "0.1.0+2-e3944897",
  libraryDependencies += "com.lynbrookrobotics" %%% "ntcore-scala-native" % "0.1.0+2-e3944897",
  libraryDependencies += "com.lynbrookrobotics" %%% "phoenix-scala-native" % "0.1.0+2-e3944897",
  scalaVersion := "2.11.12",
  scalacOptions ++= Seq("-target:jvm-1.8")
)

lazy val jvm = robot.jvm.enablePlugins(FRCPlugin).settings(
  teamNumber := 846
)

val boehmFolder = file("/Users/shadaj/cross-compile/bdwgc")
val libunwindFolder = file("/Users/shadaj/cross-compile/libunwind-1.2.1")
val librtFolder = file("/Users/shadaj/cross-compile/re2")

import scala.scalanative.sbtplugin.ScalaNativePluginInternal._
import scala.scalanative.sbtplugin.Utilities._

val crossCompileSettings = Seq(
  // fork to link with gcc instead of clang
  nativeLinkLL in Compile := {
    val linked      = (nativeLinkNIR in Compile).value
    val logger      = streams.value.log
    val apppaths    = (nativeCompileLL in Compile).value
    val nativelib   = (nativeCompileLib in Compile).value
    val cwd         = (nativeWorkdir in Compile).value
    val target      = nativeTarget.value
    val gc          = nativeGC.value
    val linkingOpts = nativeLinkingOptions.value
    val clangpp     = file("/usr/local/bin/arm-frc-linux-gnueabi-gcc")//nativeClangPP.value
    val outpath     = (artifactPath in nativeLink in Compile).value

    val links = {
      val os   = target.split("-")(2)//Option(sys props "os.name").getOrElse("")
      val arch = target.split("-").head
      // we need re2 to link the re2 c wrapper (cre2.h)
      val librt = Seq.empty // we want to statically link librt
      val libunwind = Seq.empty //Seq("unwind", "unwind-" + arch) we want to statically link libunwind

      librt ++ libunwind ++ linked.links
        .map(_.name)// ++ garbageCollector(gc).links
    }

    val linkopts  = links.map("-l" + _) ++ linkingOpts
    val targetopt = Seq("-target", target)
    val flags     = Seq("-o", outpath.abs) ++ linkopts// ++ targetopt
    // statically link libunwind
    val opaths    = ((nativelib ** "*.o").get.map(_.abs) ++
      (file("custom-c") ** "*.o").get.map(_.abs)) :+
      (libunwindFolder / "lib" / "libunwind.a").abs :+
      (libunwindFolder / "lib" / "libunwind-arm.a").abs :+
      (librtFolder / "lib" / "libre2.a").abs :+
      (boehmFolder / "gc.a").abs

    val paths     = apppaths.map(_.abs) ++ opaths
    val compile   = clangpp.abs +: (flags ++ paths)

    logger.time("Linking native code") {
      logger.running(compile)
      Process(compile, cwd) ! logger
    }

    outpath
  },
  nativeTarget := "arm-frc-linux-gnueabi",
  nativeCompileOptions ++= Seq(
    "-funwind-tables", "-target", "armv7a-frc-linux-gnueabi",
    "-mfpu=neon", "-mfloat-abi=soft",
    "--sysroot=/usr/local/arm-frc-linux-gnueabi",
    s"-I${(libunwindFolder / "include").abs}", s"-I${(librtFolder / "include").abs}", s"-I${(boehmFolder / "include").abs}",
    "-I/usr/local/arm-frc-linux-gnueabi/include/c++/5.5.0", "-I/usr/local/arm-frc-linux-gnueabi/include/c++/5.5.0/arm-frc-linux-gnueabi",
    "-I/Users/shadaj/external-dev/allwpilib/wpilibj/src/arm-linux-jni",
    "-I/Users/shadaj/external-dev/allwpilib/wpilibj/src/arm-linux-jni/linux"
  ),
  nativeLinkingOptions ++= Seq(
    "-lm", "-lc", "-lstdc++", "-lpthread", "-ldl", // system stuff,
    // transitive dependencies
    "-lwpilibJNI", "-lntcore", "-lCTRE_PhoenixCCI", "-lwpiHal", "-lwpiutil", "-l:libniriosession.so.17.0.0", "-l:libniriodevenum.so.17.0.0",
    "-l:libRoboRIO_FRC_ChipObject.so.18.0.0", "-l:libvisa.so", "-l:libFRC_NetworkCommunication.so.18.0.0",
    "-l:libNiFpga.so.17.0.0", "-l:libNiFpgaLv.so.17.0.0", "-l:libNiRioSrv.so.17.0.0",

    "-L/Users/shadaj/wpilib/common/current/lib/linux/athena/shared",
    "-L/Users/shadaj/wpilib/user/java/lib",
    "-L/Users/shadaj/wpilib/cpp/current/reflib/linux/athena/shared"
  )
)

lazy val native = robot.native.enablePlugins(ScalaNativePlugin).settings(
  nativeMode := "debug",
  nativeGC := "boehm",
  crossCompileSettings
)

// after copying, run:
// rm -f FRCUserProgram; mv robotnative-out FRCUserProgram; . /etc/profile.d/natinst-path.sh; chown lvuser FRCUserProgram; setcap 'cap_sys_nice=pe' FRCUserProgram; chmod a+x FRCUserProgram; /usr/local/frc/bin/frcKillRobot.sh -t -r
