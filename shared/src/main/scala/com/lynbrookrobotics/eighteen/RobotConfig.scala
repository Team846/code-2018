package com.lynbrookrobotics.eighteen

import com.lynbrookrobotics.eighteen.climber.deployment.{DeploymentConfig, DeploymentHardware}
import com.lynbrookrobotics.eighteen.climber.{ClimberWinchConfig, ClimberWinchHardware}
import com.lynbrookrobotics.eighteen.collector.rollers.{CollectorRollersConfig, CollectorRollersHardware}
import com.lynbrookrobotics.eighteen.collector.clamp.{CollectorClampConfig, CollectorClampHardware}
import com.lynbrookrobotics.eighteen.collector.pivot.{CollectorPivotConfig, CollectorPivotHardware}
import com.lynbrookrobotics.eighteen.driver.{DriverConfig, DriverHardware}
import com.lynbrookrobotics.eighteen.drivetrain.{DrivetrainConfig, DrivetrainHardware}
import com.lynbrookrobotics.eighteen.forklift.{ForkliftConfig, ForkliftHardware}
import com.lynbrookrobotics.eighteen.lift.{CubeLiftConfig, CubeLiftHardware}

case class RobotConfig(climberDeployment: DeploymentConfig,
                       climberWinch: ClimberWinchConfig,
                       collectorClamp : CollectorClampConfig,
                       collectorPivot: CollectorPivotConfig,
                       collectorRollers: CollectorRollersConfig,
                       driver: DriverConfig,
                       drivetrain: DrivetrainConfig,
                       forklift: ForkliftConfig,
                       cubeLift: CubeLiftConfig)
