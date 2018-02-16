package com.lynbrookrobotics.eighteen

import com.lynbrookrobotics.eighteen.climber.ClimberWinchHardware
import com.lynbrookrobotics.eighteen.climber.deployment.DeploymentHardware
import com.lynbrookrobotics.eighteen.collector.clamp.CollectorClampHardware
import com.lynbrookrobotics.eighteen.collector.pivot.CollectorPivotHardware
import com.lynbrookrobotics.eighteen.collector.rollers.CollectorRollersHardware
import com.lynbrookrobotics.eighteen.driver.DriverHardware
import com.lynbrookrobotics.eighteen.drivetrain.DrivetrainHardware
import com.lynbrookrobotics.eighteen.forklift.ForkliftHardware
import com.lynbrookrobotics.eighteen.lift.CubeLiftHardware
import com.lynbrookrobotics.potassium.streams.Stream

case class RobotHardware(climberDeployment: DeploymentHardware,
                         climberWinch: ClimberWinchHardware,
                         collectorClamp: CollectorClampHardware,
                         collectorPivot: CollectorPivotHardware,
                         collectorRollers: CollectorRollersHardware,
                         driver: DriverHardware,
                         drivetrain: DrivetrainHardware,
                         forklift: ForkliftHardware,
                         cubeLift: CubeLiftHardware)

object RobotHardware {
  def apply(robotConfig: RobotConfig, coreTicks: Stream[Unit]): RobotHardware = {
    import robotConfig._

    val driverHardware = DriverHardware(driver) // drivetrain depends on this

    RobotHardware(
      climberDeployment = if (climberDeployment != null) DeploymentHardware(climberDeployment) else null,
      climberWinch = if (climberWinch != null) ClimberWinchHardware(climberWinch) else null,
      collectorClamp = if (collectorClamp != null) CollectorClampHardware(collectorClamp) else null,
      collectorPivot = if (collectorPivot != null) CollectorPivotHardware(collectorPivot) else null,
      collectorRollers = if (collectorRollers != null) CollectorRollersHardware(collectorRollers) else null,
      driver = driverHardware,
      drivetrain = if (drivetrain != null) DrivetrainHardware(drivetrain, coreTicks, driverHardware) else null,
      forklift = if (forklift != null) ForkliftHardware(forklift) else null,
      cubeLift = if (cubeLift != null) CubeLiftHardware(cubeLift) else null
    )
  }
}