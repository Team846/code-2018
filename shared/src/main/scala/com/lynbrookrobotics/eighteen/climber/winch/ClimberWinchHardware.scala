package com.lynbrookrobotics.eighteen.climber.winch

import com.ctre.phoenix.motorcontrol.can.TalonSRX

final case class ClimberWinchHardware(leftMotor: TalonSRX, middleMotor: TalonSRX, rightMotor: TalonSRX)

object ClimberWinchHardware {
  def apply(config: ClimberWinchConfig): ClimberWinchHardware = {
    println(s"Creating TalonSRXs on Ports ${config.ports.leftMotorPort}, ${config.ports.rightMotorPort}, ${config.ports.middleMotorPort}")
    new ClimberWinchHardware(
      new TalonSRX(config.ports.leftMotorPort),
      new TalonSRX(config.ports.middleMotorPort),
      new TalonSRX(config.ports.rightMotorPort)
    )
  }
}
