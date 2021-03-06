package com.lynbrookrobotics.eighteen.climber.winch

import squants.Dimensionless

final case class ClimberWinchConfig(
  ports: ClimberWinchPorts,
  props: ClimberWinchProps
)

final case class ClimberWinchPorts(leftMotorPort: Int, middleMotorPort: Int, rightMotorPort: Int)

final case class ClimberWinchProps(climbingSpeed: Dimensionless)
