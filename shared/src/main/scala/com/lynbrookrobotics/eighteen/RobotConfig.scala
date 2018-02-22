package com.lynbrookrobotics.eighteen

import com.lynbrookrobotics.eighteen.climber.deployment.DeploymentConfig
import com.lynbrookrobotics.eighteen.climber.winch.ClimberWinchConfig
import com.lynbrookrobotics.eighteen.collector.rollers.CollectorRollersConfig
import com.lynbrookrobotics.eighteen.collector.clamp.CollectorClampConfig
import com.lynbrookrobotics.eighteen.collector.pivot.CollectorPivotConfig
import com.lynbrookrobotics.eighteen.driver.DriverConfig
import com.lynbrookrobotics.eighteen.drivetrain.DrivetrainConfig
import com.lynbrookrobotics.eighteen.forklift.ForkliftConfig
import com.lynbrookrobotics.eighteen.lift.CubeLiftConfig
import com.lynbrookrobotics.eighteen.lighting.LightingHardwareConfig
import com.lynbrookrobotics.potassium.frc.{LEDControllerConfig, LEDControllerHardware}

final case class RobotConfig(
  climberDeployment: Option[DeploymentConfig],
  climberWinch: Option[ClimberWinchConfig],
  collectorClamp: Option[CollectorClampConfig],
  collectorPivot: Option[CollectorPivotConfig],
  collectorRollers: Option[CollectorRollersConfig],
  driver: Option[DriverConfig],
  drivetrain: Option[DrivetrainConfig],
  forklift: Option[ForkliftConfig],
  cubeLift: Option[CubeLiftConfig],
  led: Option[LEDControllerConfig]
)
