package com.lynbrookrobotics.eighteen.climber

import com.ctre.phoenix.motorcontrol.can.TalonSRX

final case class ClimberWinchHardware(
  leftMotor: TalonSRX,
  middleMotor: TalonSRX,
  rightMotor: TalonSRX
)

object ClimberWinchHardware {
  def apply(config: ClimberWinchConfig): ClimberWinchHardware =
    new ClimberWinchHardware(
      new TalonSRX(config.leftMotorPort),
      new TalonSRX(config.middleMotorPort),
      new TalonSRX(config.rightMotorPort)
    )
}
