package com.lynbrookrobotics.eighteen.collector.clamp

import edu.wpi.first.wpilibj.Solenoid

case class CollectorClampHardware(solenoid: Solenoid)

object CollectorClampHardware {
  def apply(config: CollectorClampConfig): CollectorClampHardware =
    new CollectorClampHardware(
      new Solenoid(config.pneumaticPort)
    )
}
