package com.lynbrookrobotics.eighteen

import com.lynbrookrobotics.eighteen.driver.DriverConfig
import com.lynbrookrobotics.eighteen.drivetrain.{DrivetrainConfig, DrivetrainPorts, DrivetrainProperties}
import com.lynbrookrobotics.potassium.control.PIDConfig
import squants.{Each, Percent}
import squants.motion.{DegreesPerSecond, FeetPerSecond, FeetPerSecondSquared}
import squants.space.{Degrees, Feet, Inches, Turns}
import squants.time.Seconds
import com.lynbrookrobotics.potassium.units.GenericValue._
import com.lynbrookrobotics.potassium.units._
import com.lynbrookrobotics.eighteen.lift.{CubeLiftConfig, CubeLiftPorts, CubeLiftProperties}
import squants.electro.{Amperes, Volts}
import argonaut.Argonaut._
import com.lynbrookrobotics.potassium.vision.VisionProperties

object ConfigGenerator extends App {
  println(
    RobotConfig(
      climberDeployment = None,
      climberWinch = None,
      collectorClamp = None,
      collectorPivot = None,
      collectorRollers = None,
      driver = Some(
        DriverConfig(
          driverPort = 0,
          operatorPort = 1,
          driverWheelPort = 2,
          launchpadPort = -1
        )
      ),
      drivetrain = Some(
        DrivetrainConfig(
          ports = DrivetrainPorts(
            practiceSpeedControllers = false,
            leftPort = 12,
            rightPort = 11,
            leftFollowerPort = 14,
            rightFollowerPort = 13
          ),
          props = DrivetrainProperties(
            maxLeftVelocity = FeetPerSecond(18.8),
            maxRightVelocity = FeetPerSecond(19.25),
            leftVelocityGains = PIDConfig(
              Ratio(Percent(0), FeetPerSecond(5)),
              Ratio(Percent(0), Feet(5)),
              Ratio(Percent(0), FeetPerSecondSquared(5))
            ),
            rightVelocityGains = PIDConfig(
              Ratio(Percent(0), FeetPerSecond(5)),
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
            maxCurrent = Amperes(25),
            maxAcceleration = FeetPerSecondSquared(0),
            maxDeceleration = FeetPerSecondSquared(0),
            defaultLookAheadDistance = Feet(2.5),
            blendExponent = 0,
            track = Inches(21.75),
            wheelDiameter = Inches(6),
            wheelOverEncoderGears = Ratio(
              Turns(18),
              Turns(74)
            )
          )
        )
      ),
      forklift = None,
      cubeLift = Some(
        CubeLiftConfig(
          ports = CubeLiftPorts(20),
          props = CubeLiftProperties(
            pidConfig = PIDConfig(
              Percent(0) / Feet(5),
              Percent(0) / (Feet(5) * Seconds(1)),
              Percent(0) / FeetPerSecond(5)
            ),
            voltageOverHeight = Ratio(Volts(2.5), Inches(42)),
            talonOverVoltage = Each(1023) / Volts(3.3),
            voltageAtBottom = Volts(2.94),
            collectHeight = Inches(10),
            switchHeight = Inches(20),
            lowScaleHeight = Inches(30),
            highScaleHeight = Inches(30),
            exchangeHeight = Inches(4),
            liftPositionTolerance = Inches(2),
            maxMotorOutput = Percent(20),
            maxHeight = Inches(35),
            minHeight = Inches(10),
            twistyTotalRange = Feet(1),
            maxCurrent = Amperes(35)
          )
        )
      ),
      limelight = Some(VisionProperties(Degrees(0), Feet(12.0176))),
      led = None
    ).jencode(RobotConfig.writer).spaces2
  )
}
