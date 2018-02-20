package com.lynbrookrobotics.eighteen

import java.io.{File, FileWriter, PrintWriter}

import argonaut.Argonaut._
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
import squants.electro.Volts
import squants.motion.{DegreesPerSecond, FeetPerSecond, FeetPerSecondSquared}
import squants.space.{Degrees, Feet, Inches}
import squants.time.Seconds
import squants.{Each, Percent}
import argonaut.Argonaut._
import argonaut._
import ArgonautShapeless._
import com.lynbrookrobotics.potassium.config.SquantsPickling._

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

  implicit def vToOption[T](v: T): Option[T] = Some(v)
<<<<<<< HEAD
  implicit var configJson = configString
    .decodeOption[RobotConfig]
    .getOrElse {
      println("ERROR DEFAULTING CONFIG")
      configString = ""
=======
>>>>>>> f0e3dac5b8b147d871b3b3df3fa99a4b64d0bbe5

  implicit var configJson = (
      RobotConfig(
        climberDeployment = None,
        climberWinch = None,
        collectorClamp = None,
        collectorPivot = None,
        collectorRollers = None,
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
              Ratio(Percent(100), FeetPerSecond(5)),
              Ratio(Percent(0), Feet(5)),
              Ratio(Percent(0), FeetPerSecondSquared(5))
            ),
            rightVelocityGains = PIDConfig(
              Ratio(Percent(100), FeetPerSecond(5)),
              Ratio(Percent(0), Feet(5)),
              Ratio(Percent(0), FeetPerSecondSquared(5))
            ),
            forwardPositionGains = PIDConfig(
              Percent(100) / Feet(4),
              Percent(0) / (Feet(5) * Seconds(1)),
              Percent(0) / FeetPerSecond(5)
            ),
            turnVelocityGains = PIDConfig(
              Percent(0) / DegreesPerSecond(1),
              Percent(0) / (DegreesPerSecond(1) * Seconds(1)),
              Percent(0) / (toGenericValue(DegreesPerSecond(1)) / Seconds(1))
            ),
            turnPositionGains = PIDConfig(
              Percent(100) / Degrees(50),
              Percent(0) / (Degrees(1) * Seconds(1)),
              Percent(0) / (Degrees(1) / Seconds(1))
            ),
            maxTurnVelocity = DegreesPerSecond(5),
            maxAcceleration = FeetPerSecondSquared(4),
            defaultLookAheadDistance = Feet(2.5),
            blendExponent = 0,
            track = Inches(21.75)
          )
        ),
        forklift = None,
        cubeLift = CubeLiftConfig(
          ports = CubeLiftPorts(20),
          props = CubeLiftProperties(
            pidConfig = PIDConfig(
              Percent(0) / Feet(5),
              Percent(0) / (Feet(5) * Seconds(1)),
              Percent(0) / FeetPerSecond(5)
            ),
            voltageOverHeight = Ratio(Volts(2.5), Inches(42)),
            talonOverVoltage = Ratio(Each(1023), Volts(3.3)),
            voltageAtBottom = Volts(2.94),
            collectHeight = Inches(10),
            switchHeight = Inches(20),
            scaleHeight = Inches(30),
            switchTolerance = Inches(2),
            maxMotorOutput = Percent(20),
            maxHeight = Inches(30),
            minHeight = Inches(15)
          )
        )
      )
    }

  implicit val configSig = Signal(configJson)

  implicit val hardware: RobotHardware = RobotHardware(configJson, coreTicks)

  override def startCompetition(): Unit = {
    coreRobot = new CoreRobot(
      Signal(configString),
      newS => {
        val parsed = newS.decodeOption[RobotConfig]
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
