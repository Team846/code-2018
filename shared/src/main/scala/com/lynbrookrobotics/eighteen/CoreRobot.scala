package com.lynbrookrobotics.eighteen

import com.lynbrookrobotics.eighteen.collector.rollers.CollectorRollers
import com.lynbrookrobotics.eighteen.driver.DriverHardware
import com.lynbrookrobotics.eighteen.drivetrain.DrivetrainComp
import com.lynbrookrobotics.potassium.{Component, Signal}
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.tasks.ContinuousTask
import squants.Each

class CoreRobot (configFileValue: Signal[String], updateConfigFile: String => Unit, val coreTicks: Stream[Unit])
                (implicit val config: Signal[RobotConfig], hardware: RobotHardware,
                 val clock: Clock) {
  implicit val driverHardware: DriverHardware = hardware.driver
  private val ds = driverHardware.station

  implicit val drivetrainHardware = hardware.drivetrain
  implicit val drivetrainProps = config.map(_.drivetrain.props)
  val drivetrain: Option[DrivetrainComp] =
    Option(hardware.drivetrain).map(_ => new DrivetrainComp(coreTicks))

  implicit val collectorRollersHardware = hardware.collectorRollers
  implicit val collectorRollersProps = config.map(_.collectorRollers.props)
  val collectorRollers : Option[CollectorRollers] =
    Option(hardware.collectorRollers).map(_ => new CollectorRollers(coreTicks))

  lazy val components: Seq[Component[_]] = Seq(
    drivetrain,
    collectorRollers
  ).flatten

  import driverHardware._

  isTeleopEnabled.onStart.foreach { () =>
    components.foreach(_.resetToDefault())
  }

  isEnabled.onStart.foreach { () =>
    if (drivetrain.isDefined) {
      drivetrainHardware.gyro.endCalibration()
    }
  }

  isEnabled.onEnd.foreach { () =>
    components.foreach(_.resetToDefault())
  }


  for {
    collectorRollers <- collectorRollers
  } {
    driverHardware.joystickStream.eventWhen(_ => driverHardware.driverJoystick.getRawButton(1)).foreach(new ContinuousTask {
      override protected def onStart(): Unit = {
        collectorRollers.setController(coreTicks.mapToConstant(
          (Each(0.5), Each(-0.5))
        ))
      }

      override protected def onEnd(): Unit = {
        collectorRollers.resetToDefault()
      }
    })
  }
}
