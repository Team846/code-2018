package com.lynbrookrobotics.eighteen

import com.lynbrookrobotics.eighteen.climber.deployment.DeploymentHardware
import com.lynbrookrobotics.eighteen.climber.winch.ClimberWinchHardware
import com.lynbrookrobotics.eighteen.collector.clamp.CollectorClampHardware
import com.lynbrookrobotics.eighteen.collector.pivot.CollectorPivotHardware
import com.lynbrookrobotics.eighteen.collector.rollers.CollectorRollersHardware
import com.lynbrookrobotics.eighteen.driver.DriverHardware
import com.lynbrookrobotics.eighteen.drivetrain.DrivetrainHardware
import com.lynbrookrobotics.eighteen.drivetrain.slam.XVLidar
import com.lynbrookrobotics.eighteen.forklift.ForkliftHardware
import com.lynbrookrobotics.eighteen.lift.CubeLiftHardware
import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.frc.{LEDControllerHardware, WPIClock}
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.vision.limelight.LimeLightHardware
import edu.wpi.first.wpilibj.SerialPort

final case class RobotHardware(
  climberDeployment: Option[DeploymentHardware],
  climberWinch: Option[ClimberWinchHardware],
  collectorClamp: Option[CollectorClampHardware],
  collectorPivot: Option[CollectorPivotHardware],
  collectorRollers: Option[CollectorRollersHardware],
  driver: DriverHardware,
  drivetrain: Option[DrivetrainHardware],
  forklift: Option[ForkliftHardware],
  cubeLift: Option[CubeLiftHardware],
  camera: Option[LimeLightHardware],
  ledHardware: Option[LEDControllerHardware],
  lidar: Option[XVLidar]
)

object RobotHardware {
  def apply(robotConfig: RobotConfig, coreTicks: Stream[Unit]): RobotHardware = {
    import robotConfig._

    val driverHardware = DriverHardware(robotConfig.driver.get) // drivetrain depends on this

    RobotHardware(
      climberDeployment = climberDeployment.map(DeploymentHardware.apply),
      climberWinch = climberWinch.map(ClimberWinchHardware.apply),
      collectorClamp = collectorClamp.map(CollectorClampHardware.apply(_, coreTicks)),
      collectorPivot = collectorPivot.map(CollectorPivotHardware.apply),
      collectorRollers = collectorRollers.map(CollectorRollersHardware.apply),
      driver = driverHardware,
      drivetrain = robotConfig.drivetrain.map(DrivetrainHardware.apply(_, coreTicks, driverHardware)),
      forklift = robotConfig.forklift.map(ForkliftHardware.apply),
      cubeLift = robotConfig.cubeLift.map(CubeLiftHardware.apply(_, coreTicks)),
      camera = robotConfig.limelight.map { l =>
        new LimeLightHardware(true)(WPIClock, Signal.constant(l))
      },
      ledHardware = robotConfig.led.map(l => LEDControllerHardware(l)),
      lidar = Some(new XVLidar(SerialPort.Port.kUSB))
    )
  }
}
