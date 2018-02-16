package com.lynbrookrobotics.eighteen.forklift

import edu.wpi.first.wpilibj.Solenoid

final case class ForkliftHardware(solenoidLeft: Solenoid, solenoidRight: Solenoid)

object ForkliftHardware {
  def apply(config: ForkliftConfig): ForkliftHardware = {
    ForkliftHardware(
      new Solenoid(config.solenoidLeftPort),
      new Solenoid(config.solenoidRightPort)
    )
  }
}
