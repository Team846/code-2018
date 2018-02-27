package com.lynbrookrobotics.eighteen

import java.io.{File, FileWriter, PrintWriter}

import com.lynbrookrobotics.eighteen.driver.DriverConfig
import com.lynbrookrobotics.eighteen.drivetrain.{DrivetrainConfig, DrivetrainPorts, DrivetrainProperties}
import com.lynbrookrobotics.eighteen.lift.{CubeLiftConfig, CubeLiftPorts, CubeLiftProperties}
import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.control.PIDConfig
import com.lynbrookrobotics.potassium.frc.WPIClock
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.units.GenericValue._
import com.lynbrookrobotics.potassium.units._
import edu.wpi.first.wpilibj.RobotBase
import edu.wpi.first.wpilibj.hal.HAL
import squants.electro.{Amperes, Volts}
import squants.motion.{DegreesPerSecond, FeetPerSecond, FeetPerSecondSquared}
import squants.space.{Degrees, Feet, Inches}
import squants.time.Seconds
import squants.{Each, Percent}
import argonaut.Argonaut._

import scala.io.Source
import scala.util.Try

class LaunchRobot extends RobotBase {
  implicit val clock = WPIClock

  private var coreRobot: CoreRobot = null

  private val ds = m_ds

  val coreTicks = Stream.periodic(Seconds(0.01))(())

  val configFile = new File("/home/lvuser/robot-config.json")

  var configString = Try(
    Source.fromFile(configFile).mkString
  ).getOrElse("")

  implicit var configJson = configString
    .decodeOption[RobotConfig](RobotConfig.reader)
    .getOrElse {
      println("ERROR DEFAULTING CONFIG")
      configString = DefaultConfig.json
      DefaultConfig.json.decodeOption[RobotConfig](RobotConfig.reader).get
    }

  implicit val configSig = Signal(configJson)

  implicit val hardware: RobotHardware = RobotHardware(configJson, coreTicks)

  override def startCompetition(): Unit = {
    coreRobot = new CoreRobot(
      Signal(configString),
      newS => {
        val parsed = newS.decodeOption[RobotConfig](RobotConfig.reader)
        if (parsed.isEmpty) {
          println("COULD NOT PARSE NEW CONFIG")
        } else {
          parsed.foreach { it =>
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
