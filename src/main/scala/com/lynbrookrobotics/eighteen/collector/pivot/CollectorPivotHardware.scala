package com.lynbrookrobotics.eighteen.collector.pivot

import edu.wpi.first.wpilibj.Solenoid

case class CollectorPivotHardware(solenoid: Solenoid)

object CollectorPivotHardware {
  def apply(config: CollectorPivotConfig): CollectorPivotHardware = {
    CollectorPivotHardware(
      new Solenoid(config.pneumaticPort)
    )
  }
}
