package com.lynbrookrobotics.eighteen

import com.lynbrookrobotics.eighteen.climber.deployment.DeploymentConfig
import com.lynbrookrobotics.eighteen.climber.ClimberWinchConfig
import com.lynbrookrobotics.eighteen.collector.rollers.CollectorRollersConfig
import com.lynbrookrobotics.eighteen.collector.clamp.CollectorClampConfig
import com.lynbrookrobotics.eighteen.collector.pivot.CollectorPivotConfig
import com.lynbrookrobotics.eighteen.driver.DriverConfig
import com.lynbrookrobotics.eighteen.drivetrain.DrivetrainConfig
import com.lynbrookrobotics.eighteen.forklift.ForkliftConfig
import com.lynbrookrobotics.eighteen.lift.CubeLiftConfig

final case class RobotConfig(
  climberDeployment: DeploymentConfig,
  climberWinch: ClimberWinchConfig,
  collectorClamp: CollectorClampConfig,
  collectorPivot: CollectorPivotConfig,
  collectorRollers: CollectorRollersConfig,
  driver: DriverConfig,
  drivetrain: DrivetrainConfig,
  forklift: ForkliftConfig,
  cubeLift: CubeLiftConfig
)
