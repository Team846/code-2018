package com.lynbrookrobotics.eighteen

import com.lynbrookrobotics.eighteen.collector.rollers.CollectorRollersHardware
import com.lynbrookrobotics.eighteen.collector.clamp.CollectorClampHardware
import com.lynbrookrobotics.eighteen.driver.DriverHardware
import com.lynbrookrobotics.eighteen.drivetrain.DrivetrainHardware
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.streams.Stream

final case class RobotHardware(
  driver: DriverHardware,
  drivetrain: DrivetrainHardware,
  collectorRollers: CollectorRollersHardware,
  collectorClamp: CollectorClampHardware
)

object RobotHardware {
  def apply(robotConfig: RobotConfig, coreTicks: Stream[Unit])(
    implicit clock: Clock
  ): RobotHardware = {
    import robotConfig._
    val driverHardware = DriverHardware(robotConfig.driver)

    RobotHardware(
      driver = driverHardware,
      drivetrain = if (drivetrain != null) DrivetrainHardware(drivetrain, coreTicks, driverHardware) else null,
      collectorRollers = if (collectorRollers != null) CollectorRollersHardware(collectorRollers) else null,
      collectorClamp = if (collectorClamp != null) CollectorClampHardware(collectorClamp) else null
    )
  }
}
