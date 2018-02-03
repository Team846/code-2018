package com.lynbrookrobotics.eighteen.collector.rollers

import edu.wpi.first.wpilibj.Spark

case class CollectorRollersHardware(rollerLeft: Spark, rollerRight: Spark)

object CollectorRollersHardware {
  def apply(config: CollectorRollersConfig): CollectorRollersHardware = {
    new CollectorRollersHardware(
      new Spark(config.rollerLeftPort),
      new Spark(config.rollerRightPort)
    )
  }
}
