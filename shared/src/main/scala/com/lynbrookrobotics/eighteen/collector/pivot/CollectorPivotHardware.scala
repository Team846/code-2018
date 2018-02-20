package com.lynbrookrobotics.eighteen.collector.pivot

import edu.wpi.first.wpilibj.Solenoid

final case class CollectorPivotHardware(pivotSolenoid: Solenoid)

object CollectorPivotHardware {
  def apply(config: CollectorPivotConfig): CollectorPivotHardware = {
    CollectorPivotHardware(
      new Solenoid(config.pneumaticPort)
    )
  }
}
