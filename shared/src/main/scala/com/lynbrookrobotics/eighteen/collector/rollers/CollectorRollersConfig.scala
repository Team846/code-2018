package com.lynbrookrobotics.eighteen.collector.rollers

import squants.Dimensionless

case class CollectorRollersConfig(ports: CollectorRollersPorts, props: CollectorRollersProperties)
case class CollectorRollersPorts(rollerLeftPort: Int, rollerRightPort: Int)
case class CollectorRollersProperties(collectSpeed: Dimensionless)
