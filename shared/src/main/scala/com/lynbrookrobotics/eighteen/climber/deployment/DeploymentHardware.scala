package com.lynbrookrobotics.eighteen.climber.deployment

import edu.wpi.first.wpilibj.Solenoid

final case class DeploymentHardware(solenoidLeft: Solenoid, solenoidRight: Solenoid)

object DeploymentHardware {
  def apply(config: DeploymentConfig): DeploymentHardware = {
    DeploymentHardware(
      new Solenoid(config.solenoidLeftPort),
      new Solenoid(config.solenoidRightPort)
    )
  }
}
