package com.lynbrookrobotics.eighteen.driver

import com.lynbrookrobotics.potassium.events.ContinuousEvent
import com.lynbrookrobotics.potassium.frc.Implicits._
import com.lynbrookrobotics.potassium.streams.Stream
import edu.wpi.first.wpilibj.{DriverStation, Joystick}
import squants.Dimensionless

case class JoystickState(x: Dimensionless, y: Dimensionless)
case class JoystickValues(
  driver: JoystickState,
  driverWheel: JoystickState,
  operator: JoystickState
)

case class DriverHardware(
  driverJoystick: Joystick,
  operatorJoystick: Joystick,
  driverWheel: Joystick,
  launchpad: Joystick,
  station: DriverStation
) {
  val (driverStationTicks, driverStationUpdate) = Stream.manual[Unit]
  val joystickStream: Stream[JoystickValues] = driverStationTicks.map { _ =>
    JoystickValues(
      driver = JoystickState(driverJoystick.x, driverJoystick.y),
      driverWheel = JoystickState(driverWheel.x, driverWheel.y),
      operator = JoystickState(operatorJoystick.x, operatorJoystick.y)
    )
  }

  val enabledState: Stream[Boolean] = driverStationTicks.map(_ => station.isEnabled)
  val autonomousState: Stream[Boolean] = driverStationTicks.map(_ => station.isAutonomous)
  val teleopState: Stream[Boolean] = driverStationTicks.map(_ => station.isOperatorControl)

  val isEnabled: ContinuousEvent = enabledState.eventWhen(identity)
  val isAutonomous: ContinuousEvent = autonomousState.eventWhen(identity)
  val isTeleop: ContinuousEvent = teleopState.eventWhen(identity)

  val isAutonomousEnabled: ContinuousEvent = isEnabled && isAutonomous
  val isTeleopEnabled: ContinuousEvent = isEnabled && isTeleop
}

object DriverHardware {
  def apply(config: DriverConfig): DriverHardware =
    DriverHardware(
      new Joystick(config.driverPort),
      new Joystick(config.operatorPort),
      new Joystick(config.driverWheelPort),
      new Joystick(config.launchpadPort),
      DriverStation.getInstance()
    )
}
