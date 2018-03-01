package com.lynbrookrobotics.eighteen.collector.pivot

import edu.wpi.first.wpilibj.Solenoid

final case class CollectorPivotHardware(pivotSolenoid: Solenoid)

object CollectorPivotHardware {
  def apply(config: CollectorPivotConfig): CollectorPivotHardware = {
    CollectorPivotHardware(
      {
        println(s"[DEBUG] Creating pivot solenoid on port ${config.pneumaticPort}")
        new Solenoid(config.pneumaticPort)
      }
    )
  }
}
