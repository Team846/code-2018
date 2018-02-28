package com.lynbrookrobotics.eighteen.collector.rollers

import edu.wpi.first.wpilibj.Spark

final case class CollectorRollersHardware(rollerLeft: Spark, rollerRight: Spark)

object CollectorRollersHardware {
  def apply(config: CollectorRollersConfig): CollectorRollersHardware = {
    println(s"Creating new Spark on ${config.ports.rollerLeftPort}, and ${config.ports.rollerRightPort}")
    new CollectorRollersHardware(
      new Spark(config.ports.rollerLeftPort),
      new Spark(config.ports.rollerRightPort)
    )
  }
}
