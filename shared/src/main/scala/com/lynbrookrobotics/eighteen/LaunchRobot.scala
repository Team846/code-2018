package com.lynbrookrobotics.eighteen

import argonaut.Argonaut._
import argonaut._
import ArgonautShapeless._
import com.lynbrookrobotics.potassium.config.SquantsPickling._
import com.lynbrookrobotics.eighteen.collector.rollers.{CollectorRollersConfig, CollectorRollersPorts, CollectorRollersProperties}
import com.lynbrookrobotics.eighteen.driver.DriverConfig
import com.lynbrookrobotics.eighteen.drivetrain.{DrivetrainConfig, DrivetrainPorts, DrivetrainProperties}
import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.control.PIDConfig
import com.lynbrookrobotics.potassium.frc.WPIClock
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.units.GenericValue._
import com.lynbrookrobotics.potassium.units._
import GenericValue._
import com.lynbrookrobotics.eighteen.collector.rollers.{CollectorRollersConfig, CollectorRollersPorts, CollectorRollersProperties}
import com.lynbrookrobotics.eighteen.collector.clamp.CollectorClampConfig
import squants.time.Seconds
import edu.wpi.first.wpilibj.RobotBase
import edu.wpi.first.wpilibj.hal.HAL
import squants.Percent
import squants.motion.{DegreesPerSecond, FeetPerSecond, FeetPerSecondSquared}
import squants.space.{Degrees, Feet, Inches}
import squants.time.Seconds

import scala.io.Source
import java.io.{File, FileWriter, PrintStream, PrintWriter}

import scala.util.Try

class LaunchRobot extends RobotBase {
  implicit val clock = WPIClock

  private var coreRobot: CoreRobot = null

  private val ds = m_ds

  val coreTicks = Stream.periodic(Seconds(0.01))(())

  val configFile = new File("/home/lvuser/robot-config.json")

  var configString = Try (
    Source.fromFile(configFile).mkString
    ).getOrElse("")

  implicit var configJson = configString.decodeOption[RobotConfig].getOrElse(
    RobotConfig(
      driver = DriverConfig(
        driverPort = 0,
        operatorPort = 1,
        driverWheelPort = 2,
        launchpadPort = -1
      ),
      drivetrain = DrivetrainConfig(
        ports = DrivetrainPorts(
          leftPort = 50,
          rightPort = 41,
          leftFollowerPort = 51,
          rightFollowerPort = 40
        ),
        props = DrivetrainProperties(
          maxLeftVelocity = FeetPerSecond(18.8),
          maxRightVelocity = FeetPerSecond(19.25),
          leftVelocityGains = PIDConfig(
            Ratio(Percent(40), FeetPerSecond(5)),
            Ratio(Percent(0), Feet(5)),
            Ratio(Percent(0), FeetPerSecondSquared(5))
          ),
          rightVelocityGains = PIDConfig(
            Ratio(Percent(40), FeetPerSecond(5)),
            Ratio(Percent(0), Feet(5)),
            Ratio(Percent(0), FeetPerSecondSquared(5))
          ),
          forwardPositionGains = PIDConfig(
            Percent(0) / Feet(5),
            Percent(0) / (Feet(5) * Seconds(1)),
            Percent(0) / FeetPerSecond(5)
          ),
          turnVelocityGains = PIDConfig(
            Percent(0) / DegreesPerSecond(1),
            Percent(0) / (DegreesPerSecond(1) * Seconds(1)),
            Percent(0) / (toGenericValue(DegreesPerSecond(1)) / Seconds(1))
          ),
          turnPositionGains = PIDConfig(
            Percent(0) / Degrees(1),
            Percent(0) / (Degrees(1) * Seconds(1)),
            Percent(0) / (Degrees(1) / Seconds(1))
          ),
          maxTurnVelocity = DegreesPerSecond(90),
          maxAcceleration = FeetPerSecondSquared(0),
          defaultLookAheadDistance = Feet(2.5),
          blendExponent = 0,
          track = Inches(21.75)
        ),
        idx = 0
      ),
      collectorRollers = CollectorRollersConfig(
        ports = CollectorRollersPorts(
          rollerLeftPort = 20,
          rollerRightPort = 21
        ),
        props = CollectorRollersProperties(
          collectSpeed = Percent(50)
        )
      ),
      collectorClamp = CollectorClampConfig(
        pneumaticPort = 1
      )
    )
  )

  implicit val configSig = Signal(configJson)

  implicit val hardware: RobotHardware = RobotHardware(configJson, coreTicks)

  override def startCompetition(): Unit = {
    coreRobot = new CoreRobot(
      Signal(configString),
      newS => newS.decodeOption[RobotConfig].foreach { it =>
        println("writing to robot-config.json")
        configString = newS
        configJson = it

        val writer = new PrintWriter(new FileWriter(configFile))
        writer.println(configString)
        writer.close()
      },
      coreTicks
    )

    HAL.observeUserProgramStarting()

    println("------------------------------------------\n" +
      "Finished preloading and establishing connections. " +
      "Wait 5 seconds to allow for sensor calibration\n")

    while (true) {
      ds.waitForData()
      coreRobot.driverHardware.driverStationUpdate.apply()
    }
  }
}
