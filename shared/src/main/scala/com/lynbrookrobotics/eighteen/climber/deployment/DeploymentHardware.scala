package com.lynbrookrobotics.eighteen.climber.deployment

import edu.wpi.first.wpilibj.Solenoid

final case class DeploymentHardware(deploymentSolenoid: Solenoid)

object DeploymentHardware {
  def apply(config: DeploymentConfig): DeploymentHardware = {
    DeploymentHardware(
      {
        println(s"[DEBUG] Creating climber deployment solenoid on port ${config.solenoidPort}")
        new Solenoid(config.solenoidPort)
      }
    )
  }
}
