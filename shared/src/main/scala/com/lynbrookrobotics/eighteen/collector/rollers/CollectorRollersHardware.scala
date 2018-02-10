package com.lynbrookrobotics.eighteen.collector.rollers

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import edu.wpi.first.wpilibj.Spark

case class CollectorRollersHardware(rollerLeft: TalonSRX, rollerRight: TalonSRX)

object CollectorRollersHardware {
  def apply(config: CollectorRollersConfig): CollectorRollersHardware = {
    new CollectorRollersHardware(
      new TalonSRX(config.ports.rollerLeftPort),
      new TalonSRX(config.ports.rollerRightPort)
    )
  }
}