package com.lynbrookrobotics.eighteen.collector.rollers

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import edu.wpi.first.wpilibj.Spark

final case class CollectorRollersHardware(rollerLeft: /*Spark*/TalonSRX, rollerRight: /*Spark*/TalonSRX)

object CollectorRollersHardware {
  def apply(config: CollectorRollersConfig): CollectorRollersHardware = {
    new CollectorRollersHardware(
      new /*Spark*/TalonSRX(config.ports.rollerLeftPort),
      new /*Spark*/TalonSRX(config.ports.rollerRightPort)
    )
  }
}
