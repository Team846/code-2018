package com.lynbrookrobotics.eighteen

import com.lynbrookrobotics.eighteen.collector.rollers.CollectorRollers
import com.lynbrookrobotics.eighteen.collector.clamp.CollectorClamp
import com.lynbrookrobotics.eighteen.driver.DriverHardware
import com.lynbrookrobotics.eighteen.drivetrain.DrivetrainComponent
import com.lynbrookrobotics.potassium.{Component, Signal}
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.streams.Stream

class CoreRobot(configFileValue: Signal[String], updateConfigFile: String => Unit, val coreTicks: Stream[Unit])(
  implicit val config: Signal[RobotConfig],
  hardware: RobotHardware,
  val clock: Clock
) {
  implicit val driverHardware: DriverHardware = hardware.driver

  implicit val drivetrainHardware = hardware.drivetrain
  implicit val drivetrainProps = config.map(_.drivetrain.props)
  val drivetrain: Option[DrivetrainComponent] =
    Option(hardware.drivetrain).map(_ => new DrivetrainComponent(coreTicks))

  implicit val collectorRollersHardware = hardware.collectorRollers
  implicit val collectorRollersProps = config.map(_.collectorRollers.props)
  val collectorRollers: Option[CollectorRollers] =
    Option(hardware.collectorRollers).map(_ => new CollectorRollers(coreTicks))

  implicit val collectorClampHardware = hardware.collectorClamp
  implicit val collectorClampProps = config.map(_.collectorClamp)
  val collectorClamp: Option[CollectorClamp] =
    Option(hardware.collectorClamp).map(_ => new CollectorClamp(coreTicks))

  lazy val components: Seq[Component[_]] = Seq(
    drivetrain,
    collectorRollers,
    collectorClamp
  ).flatten

  // Register at the end so they are all run first
  driverHardware.isTeleopEnabled.onStart.foreach { () =>
    components.foreach(_.resetToDefault())
  }

  driverHardware.isEnabled.onStart.foreach { () =>
    if (drivetrain.isDefined) {
      drivetrainHardware.gyro.endCalibration()
    }
  }

  driverHardware.isEnabled.onEnd.foreach { () =>
    components.foreach(_.resetToDefault())
  }

  ButtonMappings.setup(this)
}
