package com.lynbrookrobotics.eighteen

import java.io.{File, FileWriter, PrintWriter}

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.frc.WPIClock
import com.lynbrookrobotics.potassium.streams.Stream
import edu.wpi.first.wpilibj.RobotBase
import edu.wpi.first.wpilibj.hal.HAL
import squants.time.Seconds
import argonaut.Argonaut._

import scala.io.Source
import scala.util.Try

class LaunchRobot extends RobotBase {

  implicit val clock = WPIClock

  private var coreRobot: CoreRobot = null

  private val ds = m_ds

  val coreTicks = Stream.periodic(Seconds(0.01))(())

  val configFile = new File("/home/lvuser/robot-config.json")

  var configString: String = Try(
    Source.fromFile(configFile).mkString
  ).getOrElse("")

  implicit var configJson = configString
    .decodeEither[RobotConfig](RobotConfig.reader)
    .fold(
      (e: String) => {
        println("ERROR DEFAULTING CONFIG")
        println(s"ERROR: $e")
        configString = DefaultConfig.json
        DefaultConfig.json
          .decodeEither[RobotConfig](RobotConfig.reader)
          .fold((e: String) => {
            throw new Exception(s"Could not parse default config: $e")
          }, identity)
      },
      identity
    )

  implicit val configSig = Signal(configJson)

  implicit val hardware: RobotHardware = RobotHardware(configJson, coreTicks)

  override def startCompetition(): Unit = {
    coreRobot = new CoreRobot(
      Signal(configString),
      newS => {
        val parsed = newS.decodeEither[RobotConfig](RobotConfig.reader)
        if (parsed.isLeft) {
          println(s"COULD NOT PARSE NEW CONFIG ${parsed.left.get}")
        } else {
          parsed.right.foreach { it =>
            println("writing to robot-config.json")
            configString = newS
            configJson = it

            val writer = new PrintWriter(new FileWriter(configFile))
            writer.println(configString)
            writer.close()
          }
        }
      },
      coreTicks
    )

    HAL.observeUserProgramStarting()

    println("before calib?")
    println(
      "------------------------------------------\n" +
        "Finished preloading and establishing connections. " +
        "Wait 5 seconds to allow for sensor calibration\n"
    )

    while (true) {
      ds.waitForData()
      coreRobot.driverHardware.driverStationUpdate.apply()
    }
  }
}
