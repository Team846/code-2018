package com.lynbrookrobotics.eighteen.climber.winch

import com.ctre.phoenix.motorcontrol.can.TalonSRX

final case class ClimberWinchHardware(leftMotor: TalonSRX, middleMotor: TalonSRX, rightMotor: TalonSRX)

object ClimberWinchHardware {
  def apply(config: ClimberWinchConfig): ClimberWinchHardware = {
    new ClimberWinchHardware(
      new TalonSRX(config.ports.leftMotorPort),
      new TalonSRX(config.ports.middleMotorPort),
      new TalonSRX(config.ports.rightMotorPort)
    )
  }
}
