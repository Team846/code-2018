package com.lynbrookrobotics.eighteen.climber

import squants.Dimensionless

final case class ClimberWinchConfig(
  leftMotorPort: Int,
  middleMotorPort: Int,
  rightMotorPort: Int,
  climbingSpeed: Dimensionless
)
