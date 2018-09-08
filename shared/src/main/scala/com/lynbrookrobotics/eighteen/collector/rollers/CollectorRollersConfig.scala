package com.lynbrookrobotics.eighteen.collector.rollers

import squants.Dimensionless
import squants.time.Frequency

final case class CollectorRollersConfig(ports: CollectorRollersPorts, props: CollectorRollersProperties)
final case class CollectorRollersPorts(rollerLeftPort: Int, rollerRightPort: Int)
final case class CollectorRollersProperties(
  collectSpeed: Dimensionless,
  purgeSpeed: Dimensionless,
  purgeSpeedAuto: Dimensionless,
  sqrWaveFreq: Frequency,
  sqrWaveAmpl: Dimensionless
)
