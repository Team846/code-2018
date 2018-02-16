package com.lynbrookrobotics.eighteen.collector.rollers

import squants.Dimensionless

final case class CollectorRollersConfig(
  ports: CollectorRollersPorts,
  props: CollectorRollersProperties
)
final case class CollectorRollersPorts(rollerLeftPort: Int, rollerRightPort: Int)
final case class CollectorRollersProperties(collectSpeed: Dimensionless)
