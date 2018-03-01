package com.lynbrookrobotics.eighteen.collector.rollers

import edu.wpi.first.wpilibj.Spark

final case class CollectorRollersHardware(rollerLeft: Spark, rollerRight: Spark)

object CollectorRollersHardware {
  def apply(config: CollectorRollersConfig): CollectorRollersHardware = {
    new CollectorRollersHardware(
      {
        println(s"Creating roller left spark on port ${config.ports.rollerLeftPort}")
        new Spark(config.ports.rollerLeftPort)
      }, {
        println(s"Creating roller right spark on port ${config.ports.rollerRightPort}")
        new Spark(config.ports.rollerRightPort)
      }
    )
  }
}
