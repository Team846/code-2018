package com.lynbrookrobotics.eighteen.climber

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import squants.Dimensionless

case class ClimberWinchConfig (leftMotorPort: Int, middleMotorPort: Int, rightMotorPort: Int, climbingSpeed: Dimensionless)
